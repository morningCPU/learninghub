"""修复后的脚本，通过 mypy --strict。"""

import json
from typing import Any

import requests


def load_config(path: str) -> dict[str, Any]:
    with open(path) as f:
        data: dict[str, Any] = json.load(f)  # 显式标注变量类型
        return data


def fetch_data(url: str) -> dict[str, Any]:
    """从 API 获取数据。"""
    response = requests.get(url, timeout=10)
    response.raise_for_status()
    data:dict[str, Any] = response.json()
    return data


def calculate_total(items: list[dict[str, Any]]) -> float:
    """计算总价。"""
    if not items:
        return 0.0

    total = 0.0
    for item in items:
        total += item["price"] * item["quantity"]
    return total


def process_order(order_id: int, config: dict[str, Any]) -> float | None:
    """处理订单。"""
    if not config.get("enabled"):
        return None

    data = fetch_data(config["api_url"] + f"/orders/{order_id}")

    results: list[dict[str, Any]] = []
    for item in data.get("items", []):
        results.append({
            "name": item["name"],
            "value": item["price"] * item["quantity"]
        })

    return calculate_total(results)


def main() -> None:
    config = load_config("config.json")
    result = process_order(123, config)
    if result is not None:
        print(f"Result: {result}")
    else:
        print("Order processing is disabled.")


if __name__ == "__main__":
    main()