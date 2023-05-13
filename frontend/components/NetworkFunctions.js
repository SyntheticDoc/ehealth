import User from "../type/User";

const IPFelix = "192.168.188.95";
const IPLarissa = "10.0.0.58";
const IPM = "10.0.0.74"



const getData = () => {

  

  fetch(":8080/data/lasthealthstatus?userName="+"&password="+"&deviceIdentifier", { method: "GET" })
    .then((response) => response.json())
    .then((responseJson) => {
      //setData(JSON.stringify(responseJson));
      // setHealthStatus(JSON.stringify(responseJson));
    })
    .catch((error) => {
      //Error
      // Alert.alert(JSON.stringify(error));
      console.error(error);
    });
};
const castEmergencyCall = () => {
  fetch(
    "http://" +
      IPLarissa +
      ':8080/user/sendsms?recipient=436649150335&message="Hey buddy, your heart is fine"',
    { method: "GET" }
  )
    .then((response) => console.log(response))
    .catch((error) => {
      //Error
      console.error(error);
    });
};




export { getData, castEmergencyCall };
