#!/usr/bin/env python

import base64
import json
import matplotlib.pyplot as plt
import pandas as pd
import sys
import threading
import traceback
from io import BytesIO

plt.rcParams["font.family"] = "Malgun Gothic"

def read_stdin_with_timeout(timeout=3.0):
    result = []
    def read_input():
        try:
            result.append(sys.stdin.read().strip())
        except Exception:
            result.append("")
    thread = threading.Thread(target=read_input)
    thread.start()
    thread.join(timeout)
    if thread.is_alive():
        return None
    return result[0]

def generate_daily_sales_plot(df, unit="원"):
    daily_sales = df['totalSales'].resample('D').sum()
    plt.figure(figsize=(12,6))
    plt.plot(daily_sales.index, daily_sales.values, marker='o', linestyle='-')
    plt.xlabel("Date")
    plt.ylabel(f"Total Sales ({unit})")
    plt.title("날짜 별 판매 추세")
    plt.grid()
    import matplotlib.ticker as mtick
    plt.gca().yaxis.set_major_formatter(mtick.StrMethodFormatter('{x:,.0f} ' + unit))
    buffer = BytesIO()
    plt.savefig(buffer, format="png", dpi=100)
    plt.close()
    buffer.seek(0)
    return base64.b64encode(buffer.read()).decode("utf-8")

def generate_hourly_sales_plot(df, unit="원", selected_date=None):
    if selected_date:
        selected_date_parsed = pd.to_datetime(selected_date).date()
        df = df[df.index.date == selected_date_parsed]
        if df.empty:
            raise Exception(f"No data found for selected date: {selected_date}")

    df['hour'] = df.index.hour
    hourly_sales = df.groupby('hour')['totalSales'].sum()

    plt.figure(figsize=(12,6))
    plt.plot(hourly_sales.index, hourly_sales.values, marker='o', linestyle='-')
    plt.xlabel("Hour of the Day")
    plt.ylabel(f"Total Sales ({unit})")
    title = f"{selected_date} 시간 별 판매 추세" if selected_date else "시간 별 판매 추세"
    plt.title(title)
    plt.xticks(range(0, 24))
    plt.grid()
    import matplotlib.ticker as mtick
    plt.gca().yaxis.set_major_formatter(mtick.StrMethodFormatter('{x:,.0f} ' + unit))

    buffer = BytesIO()
    plt.savefig(buffer, format="png", dpi=100)
    plt.close()
    buffer.seek(0)
    return base64.b64encode(buffer.read()).decode("utf-8")

def main():
    try:
        json_data = read_stdin_with_timeout(3.0)
        if json_data is None:
            raise Exception("No input received within timeout period")
        if not json_data:
            raise Exception("Received empty input")

        json_data = json_data.encode('utf-8').decode('utf-8-sig')
        print("DEBUG: Received input: " + json_data, file=sys.stderr)

        # 여기서 'Object jsonObj = ...' 대신 아래처럼 수정
        json_obj = json.loads(json_data)

        if isinstance(json_obj, dict) and "salesData" in json_obj:
            sales_data = json_obj["salesData"]
            selected_date = json_obj.get("selectedDate", None)
        elif isinstance(json_obj, list):
            sales_data = json_obj
            selected_date = None
        else:
            raise Exception("Input JSON must be either a list or a dict containing 'salesData' field")

        df = pd.DataFrame(sales_data)
        if 'date' not in df.columns or 'totalSales' not in df.columns:
            raise Exception("Input JSON must contain 'date' and 'totalSales' fields")
        df['date'] = pd.to_datetime(df['date'])
        df.set_index('date', inplace=True)
        # df = df.asfreq('D')

        unit = "원"
        daily_img_base64 = generate_daily_sales_plot(df, unit)
        hourly_img_base64 = generate_hourly_sales_plot(df, unit, selected_date)

        result = {
            "forecast_message": f"Hourly graph generated for {selected_date}" if selected_date else "Hourly graph generated for all data",
            "daily_image_base64": daily_img_base64,
            "hourly_image_base64": hourly_img_base64,
            "sales_unit": unit
        }
        output = json.dumps(result)
        print(output)
        sys.stdout.flush()

    except Exception as e:
        error_result = {
            "error": str(e),
            "trace": traceback.format_exc()
        }
        print(json.dumps(error_result))
        sys.stdout.flush()
        sys.exit(1)

if __name__ == "__main__":
    main()
