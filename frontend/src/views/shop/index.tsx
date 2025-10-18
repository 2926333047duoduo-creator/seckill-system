import React from "react";
import { Outlet } from "react-router-dom";

const Shop: React.FC = () => {
  return (
    <div>
      <Outlet />
      Shop Page
    </div>
  );
};

export default Shop;