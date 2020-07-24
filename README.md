SetupWizard
===========

Build with Android Studio
-------------------------
SetupWizard requires access to the system API, hence it cannot be built if you only have the public SDK.  
First,You would have to generate the libraries with all the required classes.  
The application also needs elevated privileges, so you need to sign it with the right key to replace the previous one located in the system partition. To do this:
 - Place this directory anywhere in the Android source tree
 - Generate a keystore and keystore.properties using `gen-keystore.sh`
 - You will only need to do it once, unless Android Studio can't find the required symbols.
 - Build the dependencies running `make SetupWizardStudio` from the root of the
   exTHmUI source tree. This command will add the needed libraries in
   `{the_root_of_setupwizard}/system_libs/`.

You need to do the above once, unless Android Studio can't find some symbol.
In this case, rebuild the system libraries with `make SetupWizardStudio`.
