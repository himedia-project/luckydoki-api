#!/usr/bin/env python

import sys
import traceback
import json
import pandas as pd
import matplotlib
matplotlib.use('Agg')  # Headless 환경(서버)에서 GUI 백엔드 문제 없게 설정
import matplotlib.pyplot as plt
from io import BytesIO
import base64

plt.rcParams["font.family"] = "Malgun Gothic"

def generate_daily_sales_plot(df, unit="원"):
    daily_sales = df['totalSales'].resample('D').sum()
    plt.figure(figsize=(12,6))
    plt.plot(daily_sales.index, daily_sales.values, marker='o', linestyle='-')
    plt.xlabel("Date")
    plt.ylabel(f"Total Sales ({unit})")
    plt.title("날짜 별 판매 추세")
    plt.grid()
    from matplotlib.ticker import StrMethodFormatter
    plt.gca().yaxis.set_major_formatter(StrMethodFormatter('{x:,.0f} ' + unit))
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
    from matplotlib.ticker import StrMethodFormatter
    plt.gca().yaxis.set_major_formatter(StrMethodFormatter('{x:,.0f} ' + unit))
    buffer = BytesIO()
    plt.savefig(buffer, format="png", dpi=100)
    plt.close()
    buffer.seek(0)
    return base64.b64encode(buffer.read()).decode("utf-8")

def main():
    try:
        # stdin 전체 읽기 (타임아웃 없음)
        raw_input = sys.stdin.read().strip()
        if not raw_input:
            raise ValueError("No input data received")

        # JSON 파싱
        json_obj = json.loads(raw_input)

        # salesData & selectedDate 파악
        if isinstance(json_obj, dict) and "salesData" in json_obj:
            sales_data = json_obj["salesData"]
            selected_date = json_obj.get("selectedDate", None)
        elif isinstance(json_obj, list):
            sales_data = json_obj
            selected_date = None
        else:
            raise ValueError("Input JSON must be a dict with 'salesData' or a list")

        df = pd.DataFrame(sales_data)
        if "date" not in df.columns or "totalSales" not in df.columns:
            raise ValueError("JSON must contain 'date' and 'totalSales' fields")

        df['date'] = pd.to_datetime(df['date'])
        df.set_index('date', inplace=True)

        unit = "원"
        daily_b64 = generate_daily_sales_plot(df, unit)
        hourly_b64 = generate_hourly_sales_plot(df, unit, selected_date)

        result = {
            "forecast_message": f"Hourly graph generated for {selected_date}" if selected_date else "Hourly graph generated (all dates)",
            "daily_image_base64": daily_b64,
            "hourly_image_base64": hourly_b64,
            "sales_unit": unit
        }

        # stdout으로 결과 JSON 출력
        print(json.dumps(result, ensure_ascii=False))
        sys.stdout.flush()

    except Exception as e:
        error_info = {
            "error": str(e),
            "trace": traceback.format_exc()
        }
        print(json.dumps(error_info, ensure_ascii=False))
        sys.stdout.flush()
        sys.exit(1)

if __name__ == "__main__":
    main()
