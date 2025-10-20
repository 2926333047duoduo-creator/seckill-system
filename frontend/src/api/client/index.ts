import type { ResponseData } from "../index";
import { instance } from "../index";

export const clientSeckill = (id: string): Promise<ResponseData<string>> => {
  return instance.post(`/client/seckill/${id}`);
};
