import type { ResponseData } from "../index";
import { instance } from "../index";

export const clientSeckill = (id: string): Promise<ResponseData<string>> => {
  return instance.post(`/client/seckill/${id}`);
};

export const getClientCouponList = (): Promise<ResponseData<object[]>> => {
  return instance.get("/client/list");
};
