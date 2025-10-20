import { createBrowserRouter, Navigate } from "react-router-dom";
import Cart from "../views/cart/index";
import CouponList from "../views/couponList";
import Layout from "../views/layout/index";
import Login from "../views/login/index";
import Me from "../views/me/index";
import Profile from "../views/profile/index";
import Shop from "../views/shop/index";
import UserCouponGrab from "../views/UserCouponGrab/index";

const router = createBrowserRouter([
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/userCouponGrab",
    element: <UserCouponGrab />,
  },
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        path: "",
        element: <Navigate to="/shop" replace={true} />,
      },
      {
        path: "/shop",
        element: <Shop />,
      },
      {
        path: "/cart",
        element: <Cart />,
      },
      {
        path: "/me",
        element: <Me />,
        children: [
          {
            path: "",
            element: <Navigate to="/me/profile" replace={true} />,
          },
          {
            path: "/me/profile",
            element: <Profile />,
          },
          {
            path: "/me/couponList",
            element: <CouponList />,
          },
        ],
      },
    ],
  },
]);

export default router;
