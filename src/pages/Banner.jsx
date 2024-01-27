import React from "react";
import Button from "../components/Button/Button";

function Banner() {
  return (
    <>
      <section className="bg-gradient-to-r from-blue-300 to-blue-500 flex flex-col items-center gap-3 ">
        <h1 className="md:text-5xl sm:text-4xl text-3xl text-white font-semibold mt-3 py-3">
          Meetly
        </h1>
        <p className="text-white md:text-2xl sm:text-xl text-lg text-center">
          Meetly integrates meeting management and video conferencing under a
          single platform.
        </p>
        <Button
          text={"View on GitHub"}
          link={`https://github.com/vigneshshettyin/Meetly`}
        />
      </section>
    </>
  );
}

export default Banner;
