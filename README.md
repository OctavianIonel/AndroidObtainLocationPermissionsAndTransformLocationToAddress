# ObtainLocationPermissionsAndTransformLocationToAddress
This Android application shows an example of how to obtain location (latitude and longitude) from GPS/network (using GoogleApiClient and LocationListener) and transforming these coordinates into an address.
In order to do this the location permission is requested at runtime.
If the user denies and click the checkbox to hide the location permission dialog, then the permission should be given manually and from the SETTINGS of the phone.
Afterwards, if the phone has GPS then it will ask through a dialog to enable the GPS sensor.
After the location is successfully obtained and it is transformed into an address, a custom callback will deregister and remove the location updates.
The following concepts are used: Annotation, Interface, SharedPreferences, Handler, Thread, callbacks, LocationListener.

![alternate text](https://github.com/OctavianIonel/AndroidObtainLocationPermissionsAndTransformLocationToAddress/blob/master/obtain_location_and_convert_to_address.gif)

In order to test this project, all you have to do is to clone it.

Enjoy!

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.