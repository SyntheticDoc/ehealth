import User from "../type/User";

const IPFelix = "192.168.188.95";
const IPLarissa = "10.0.0.58";

const getData = () => {
  fetch("http://catfact.ninja/fact", { method: "GET" })
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
const getUserbyId = () => {
  fetch("http://" + IPLarissa + ":8080/user/get-user?id=" + 1, {
    method: "GET",
  })
    .then((response) => {
      console.log(response);
      return response.json;
    })
    .catch((error) => {
      //Error
      console.error(error);
    });
};

const postUser = (name, address, phone, emergency, password) => {
  console.log("postUser");
  fetch(
    "http://" +
      IPLarissa +
      ":8080/user/post-user?name=" +
      name +
      "&address=" +
      address +
      "&phone=" +
      phone +
      "&emergency=" +
      emergency +
      "&password=" +
      password,
    {
      method: "POST",
    }
  )
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response;
    })
    .then((data) => {
      console.log(data);
      return data;
    })
    .catch((error) => {
      console.error("Error sending data to backend:", error);
    });
};

export { getData, castEmergencyCall, getUserbyId, postUser };
