import React from "react";

export const PrivacyPage = () => {
  return (
    <>
      <div className="md:max-w-[50rem] max-sm:max-w-[40rem] mx-auto mt-10">
        {/* Privacy Policy */}

        <section className="PrivacyPolicy mb-10   ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Privacy Policy
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              Vignesh built the Meetly app as an Open Source app. This SERVICE
              is provided by Vignesh at no cost and is intended for use as is.
            </p>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              This page is used to inform visitors regarding my policies with
              the collection, use, and disclosure of Personal Information if
              anyone decided to use my Service.
            </p>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              If you choose to use my Service, then you agree to the collection
              and use of information in relation to this policy. The Personal
              Information that I collect is used for providing and improving the
              Service. I will not use or share your information with anyone
              except as described in this Privacy Policy.
            </p>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              The terms used in this Privacy Policy have the same meanings as in
              our Terms and Conditions, which is accessible at Meetly unless
              otherwise defined in this Privacy Policy.
            </p>
          </div>
        </section>

        {/* Information Collection and Use */}

        <section className="infc_collec_use mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Information Collection and Use
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              For a better experience, while using our Service, I may require
              you to provide us with certain personally identifiable
              information, including but not limited to Email, Photo, Microphone
              Access, Camera Access. The information that I request will be
              retained on your device and is not collected by me in any way.
            </p>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              The app does use third party services that may collect information
              used to identify you.
            </p>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              Link to privacy policy of third party service providers used by
              the app
            </p>
            <a
              href=""
              target="_blank"
              className="pl-10 flex items-center gap-3"
            >
              <span className="w-2 h-2 bg-blue-500 rounded-full"></span>
              <span className="text-blue-400 hover:text-blue-600 ">
                {" "}
                Google Play Services
              </span>
            </a>
          </div>
        </section>

        {/* Log Data */}

        <section className="log_data  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Log Data
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              I want to inform you that whenever you use my Service, in a case
              of an error in the app I collect data and information (through
              third party products) on your phone called Log Data. This Log Data
              may include information such as your device Internet Protocol
              (“IP”) address, device name, operating system version, the
              configuration of the app when utilizing my Service, the time and
              date of your use of the Service, and other statistics.
            </p>
          </div>
        </section>

        {/* Cookies */}

        <section className="cookies  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Cookies
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              Cookies are files with a small amount of data that are commonly
              used as anonymous unique identifiers. These are sent to your
              browser from the websites that you visit and are stored on your
              device&#39;s internal memory.
            </p>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              This Service does not use these “cookies” explicitly. However, the
              app may use third party code and libraries that use “cookies” to
              collect information and improve their services. You have the
              option to either accept or refuse these cookies and know when a
              cookie is being sent to your device. If you choose to refuse our
              cookies, you may not be able to use some portions of this Service.
            </p>
          </div>
        </section>

        {/* Service Providers */}

        <section className="ServiceProviders  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Service Providers
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              I may employ third-party companies and individuals due to the
              following reasons:
            </p>
            <ul className="mb-5">
              <li className="pl-10 flex items-center gap-3">
                <span className="w-2 h-2 bg-slate-700 rounded-full"></span>
                <span className=" text-slate-700 ">
                  {" "}
                  To facilitate our Service;
                </span>
              </li>
              <li className="pl-10 flex items-center gap-3">
                <span className="w-2 h-2 bg-slate-700 rounded-full"></span>
                <span className=" text-slate-700 ">
                  {" "}
                  To provide the Service on our behalf;
                </span>
              </li>
              <li className="pl-10 flex items-center gap-3">
                <span className="w-2 h-2 bg-slate-700 rounded-full"></span>
                <span className=" text-slate-700 ">
                  {" "}
                  To perform Service-related services; or
                </span>
              </li>
              <li className="pl-10 flex items-center gap-3">
                <span className="w-2 h-2 bg-slate-700 rounded-full"></span>
                <span className=" text-slate-700 ">
                  {" "}
                  To assist us in analyzing how our Service is used.
                </span>
              </li>
            </ul>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              I want to inform users of this Service that these third parties
              have access to your Personal Information. The reason is to perform
              the tasks assigned to them on our behalf. However, they are
              obligated not to disclose or use the information for any other
              purpose.
            </p>
          </div>
        </section>

        {/* Security */}

        <section className="Security  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Security
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              I value your trust in providing us your Personal Information, thus
              we are striving to use commercially acceptable means of protecting
              it. But remember that no method of transmission over the internet,
              or method of electronic storage is 100% secure and reliable, and I
              cannot guarantee its absolute security.
            </p>
          </div>
        </section>

        {/* Links to Other Sites */}

        <section className="link_othsites  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Links to Other Sites
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              This Service may contain links to other sites. If you click on a
              third-party link, you will be directed to that site. Note that
              these external sites are not operated by me. Therefore, I strongly
              advise you to review the Privacy Policy of these websites. I have
              no control over and assume no responsibility for the content,
              privacy policies, or practices of any third-party sites or
              services.
            </p>
          </div>
        </section>

        {/* Child Privacy */}

        <section className="Child_Privacy  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Children&#39;s Privacy
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              These Services do not address anyone under the age of 13. I do not
              knowingly collect personally identifiable information from
              children under 13 years of age. In the case I discover that a
              child under 13 has provided me with personal information, I
              immediately delete this from our servers. If you are a parent or
              guardian and you are aware that your child has provided us with
              personal information, please contact me so that I will be able to
              do necessary actions.
            </p>
          </div>
        </section>

        {/* Changes to This Privacy Policy */}

        <section className="cjanges_Privacy_Policy  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Changes to This Privacy Policy
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              I may update our Privacy Policy from time to time. Thus, you are
              advised to review this page periodically for any changes. I will
              notify you of any changes by posting the new Privacy Policy on
              this page.
            </p>
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              This policy is effective as of 2021-07-21
            </p>
          </div>
        </section>

        {/* Contact Us */}

        <section className="Contact_Us  mb-10  ">
          <h1 className="md:text-3xl sm:text-2xl text-xl md:px-0 px-8 text-slate-700 font-semibold mb-5 ">
            Contact Us
          </h1>
          <div className="content md:px-1 px-8">
            <p className="md:text-[18px] sm:text-[16px] text-[14px] font-normal text-gray-600 mb-5">
              If you have any questions or suggestions about my Privacy Policy,
              do not hesitate to contact me at vigneshshetty.in@gmail.com.
            </p>
          </div>
        </section>
      </div>
    </>
  );
};
