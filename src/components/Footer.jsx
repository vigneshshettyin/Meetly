import React from "react";
import gihub from "../assets/github-mark.png";

function Footer() {
  return (
    <>
      <footer className="bg-gradient-to-r from-blue-300 to-blue-500">
        <section className="content py-5 flex flex-col items-center gap-3">
          <div className="flex items-center justify-center font-bold gap-2">
            <a href="https://github.com/vigneshshettyin/Meetly" target="_blank">
              <span className="text-white">Meetly</span>
            </a>
            <span className="text-black font-semibold">is maintained by</span>
            <a href="https://github.com/vigneshshettyin" target="_blank">
              <span className="text-white">vigneshshettyin. </span>
            </a>
          </div>
          <div className="github">
            <a href="https://github.com/vigneshshettyin/Meetly/tree/gh-pages" target="_blank">
              <img src={gihub} alt="gihub_logo" className="w-10" />
            </a>
          </div>
          <div className="copyright text-white">
            &#169;Copyright. All rights reversed.
          </div>
        </section>
      </footer>
    </>
  );
}

export default Footer;
