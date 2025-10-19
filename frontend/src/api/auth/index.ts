import type { AnyType } from "../../types";
import type { ResponseData } from "../index";
import { instance } from "../index";

export const authRegister = (data: object): Promise<ResponseData<object>> => {
  return instance.post("/auth/register", data);
};

export const authLogin = (data: object): Promise<ResponseData<AnyType>> => {
  return instance.post("/auth/login", data);
};
