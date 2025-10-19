import type { ResponseData } from "../index";
import { instance } from "../index";

export const addCoupon = (data: object): Promise<ResponseData<object>> => {
  return instance.post("/admin/add", data);
};

export const getCouponList = (): Promise<ResponseData<object>> => {
  return instance.get("/admin/list");
};

export const deleteCoupon = (id: string): Promise<ResponseData<object>> => {
  return instance.delete(`/admin/delete/${id}`);
};

export const updateCoupon = (data: object): Promise<ResponseData<object>> => {
  return instance.post("/admin/update", data);
};
