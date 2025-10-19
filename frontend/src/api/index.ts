import axios from "axios";
import Cookies from "js-cookie";

export interface ResponseData<T> {
  code: number;
  msg: string;
  data: T;
}

export const instance = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 5000,
});

// 添加请求拦截器
instance.interceptors.request.use(
  function (config) {
    // from Cookie get token
    const token = Cookies.get("token") || "";
    if (token) {
      // add Authorization header
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// 添加响应拦截器
instance.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数
    // 对响应数据做点什么
    return response.data;
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  }
);
