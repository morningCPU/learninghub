import time
import logging
from functools import wraps
from contextlib import contextmanager
from typing import Callable, Optional, Type, Tuple, Any
import threading

logging.basicConfig(level=logging.INFO, format="%(asctime)s | %(levelname)s | %(message)s")


def timed(func:Optional[Callable]=None):
    def decorator(fn:Callable):
        @wraps(fn)
        def wrapper(*args, **kwargs):
            start = time.perf_counter()
            try:
                res = fn(*args, **kwargs)
                return res
            finally:
                cost = time.perf_counter() - start
                logging.info(
                    dict(
                        action="func_timed",
                        func_name=fn.__name__,
                        cost_ms=round(cost * 1000,2)
                    )
                )
        return wrapper
    if func is None:
        return decorator
    else:
        return decorator(func)

def retry(
    times: int = 3,
    backoff: float = 0.5,
    recover_exceptions: Tuple[Type[Exception], ...] = (ConnectionError, TimeoutError)
):
    def decorator(fn: Callable):
        @wraps(fn)
        def wrapper(*args, **kwargs):
            attempt = 0
            while True:
                try:
                    return fn(*args, **kwargs)
                except recover_exceptions as e:
                    attempt += 1
                    if attempt > times:
                        logging.warning(f"func {fn.__name__} retry exhausted, times={times}")
                        raise e
                    logging.info(f"func {fn.__name__} retry {attempt}/{times}, sleep {backoff}s")
                    time.sleep(backoff)
                except Exception as e:
                    # 不在可恢复列表，直接抛出，不重试
                    raise e
        return wrapper
    return decorator

def rate_limit(rps: float):
    """
    rps: 每秒允许请求数
    """
    interval = 1.0 / rps
    lock = threading.Lock()
    last_ts = time.perf_counter()
    def decorator(fn: Callable):
        @wraps(fn)
        def wrapper(*args, **kwargs):
            nonlocal last_ts
            with lock:
                now = time.perf_counter()
                wait = interval - (now - last_ts)
                if wait > 0:
                    time.sleep(wait)
                last_ts = time.perf_counter()
            return fn(*args, **kwargs)
        return wrapper
    return decorator

@contextmanager
def timer(name="block"):
    start = time.perf_counter()
    try:
        yield
    finally:
        cost = time.perf_counter() - start
        logging.info(f"block [{name}] cost: {cost*1000:.2f} ms")

