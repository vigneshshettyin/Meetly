import React from "react";

import logo from "../assets/light_logo.png";

function Header() {
  return (
    <>
      <header className="bg-white  py-3 ">
        <nav>
          <ul className="flex justify-center items-center gap-5">
            <li>
              <img src={logo} alt="Meetly_logo" className="w-20" />
            </li>
            <li>
              <span className="text-4xl text-transparent bg-clip-text bg-gradient-to-r from-[#00BBF9] to-blue-500 font-bold font-display">
                {" "}
                Meetly
              </span>
            </li>
          </ul>
        </nav>
      </header>
    </>
  );
}

export default Header;
