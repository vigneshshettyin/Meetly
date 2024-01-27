import React from "react";

import "./Button.css";

const Button = ({ text, link }) => {
  return (
    <section className="Button flex justify-center items-center py-5">
      <a className="button text-white" href={link} target="_blank">
        <span className="animationline"> </span>
        <span className="animationline"> </span>
        <span className="animationline"> </span>
        <span className="animationline"> </span>
        {text}
      </a>
    </section>
  );
};

export default Button;
