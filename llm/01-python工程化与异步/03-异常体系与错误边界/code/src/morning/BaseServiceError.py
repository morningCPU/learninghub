class BaseServiceError(Exception):
    """所有自定义异常的父类"""
    code:str
    message:str

    def __init__(self, code:str, message:str):
        super().__init__(message)
        self.code = code
        self.message = message
