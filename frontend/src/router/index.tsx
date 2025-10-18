import { createBrowserRouter, Navigate } from "react-router-dom";
import Layout from "../views/layout/index";
import Login from "../views/login/index";
import Shop from "../views/shop/index";
 
const router = createBrowserRouter([
  {
    path: "/login",
    element: <Login />,
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
    ],
  },
]);

export default router;
