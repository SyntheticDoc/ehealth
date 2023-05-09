import { View, Text, StyleSheet, TouchableOpacity, Alert } from "react-native";
import React, { useEffect, useState, useRef, useContext } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-native-fontawesome";
import { faHeartPulse } from "@fortawesome/free-solid-svg-icons/faHeartPulse";
import { faGear } from "@fortawesome/free-solid-svg-icons/faGear";
import { faUser } from "@fortawesome/free-solid-svg-icons/faUser";
import { getData, castEmergencyCall, getUserbyId } from "./NetworkFunctions";
import { AppContext } from "../App";

const HomeScreen = ({ navigation }) => {
  const [healthStatus, setHealthStatus] = useState(1);
  const [countdown, setCountdown] = useState(30);
  const [name, setName] = useState("");

  const { generaluser, setGeneraluser } = useContext(AppContext);
  var timer = useRef();

  useEffect(() => {
    clearInterval(x);
    getData();
    var x = setInterval(() => {
      getData();
    }, 10000);
  }, []);
  useEffect(() => {
    if (generaluser) {
      setName(generaluser.name);
    }
  }, [generaluser]);

  useEffect(() => {
    if (healthStatus == 0) {
      Alert.alert("Making Phone API Call now, EMERGENCY");
      clearInterval(timer.current);
    }
    if (healthStatus == 1) {
      Alert.alert("Starting Alarm and Waiting for user input!");
      // if (timer.current) return;
      timer.current = setInterval(() => {
        setCountdown((previous) => {
          if (previous == 1) {
            setHealthStatus(0);
            castEmergencyCall();
            clearInterval(timer.current);
          }
          return previous - 1;
        });
      }, 1000);
    }
    if (healthStatus == 2) {
      clearInterval(timer.current);
    }
  }, [healthStatus]);

  return (
    <View style={styles.container}>
      <View style={styles.headerContainer}>
        <FontAwesomeIcon
          style={{
            color: "#fff",
          }}
          icon={faHeartPulse}
          size={50}
        />

        <Text style={styles.heading}>Guard</Text>
      </View>
      {generaluser!==null ? (
        <View>
          <Text style={styles.heading}>{generaluser.name} </Text>
        </View>
      ) : (
        <View></View>
      )}

      <TouchableOpacity
        onPress={() => {
          if (healthStatus == 2) {
            setHealthStatus(1);
          } else {
            clearInterval(timer.current);
            setHealthStatus(2);
            setCountdown(30);
          }
        }}
      >
        <View
          style={[
            styles.statusCircle,
            {
              backgroundColor:
                healthStatus == 2
                  ? "#419a49"
                  : healthStatus == 1
                  ? "#ffb347"
                  : "#d01818",
            },
          ]}
        >
          <Text
            style={{
              fontSize: 30,
              color: "#fff",
            }}
          >
            {healthStatus == 2 ? "OK" : healthStatus == 1 ? "WARN" : "SOS"}
          </Text>
        </View>
      </TouchableOpacity>

      <View style={styles.apiResponse}>
        <Text style={{ color: "white", fontSize: 20 }}>{countdown}</Text>
      </View>
      {healthStatus == 1 ? (
        <View style={styles.apiResponse}>
          <Text style={{ color: "white", fontSize: 20 }}>
            If it's a false alarm, please press the "Warn" button.
          </Text>
        </View>
      ) : (
        <View></View>
      )}

      <TouchableOpacity
        style={styles.gear}
        onPress={() => {
          navigation.navigate("Settings");
        }}
      >
        <FontAwesomeIcon
          icon={faGear}
          style={{ color: "#454545" }}
          size={50}
        ></FontAwesomeIcon>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.user}
        onPress={() => {
          navigation.navigate("Register");
        }}
      >
        <FontAwesomeIcon
          icon={faUser}
          style={{ color: "#454545" }}
          size={50}
        ></FontAwesomeIcon>
      </TouchableOpacity>
    </View>
  );
};

export default HomeScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: "column",
    backgroundColor: "#93CAED",
    color: "white",
    alignItems: "center",
    justifyContent: "flex-start",
  },
  heading: {
    color: "white",
    fontSize: 50,
  },
  headerContainer: {
    marginTop: 100,
    flexDirection: "row",
    alignItems: "center",
  },
  gear: {
    position: "absolute",
    bottom: 0,
    right: 0,
    margin: 60,
  },
  user: {
    position: "absolute",
    bottom: 0,
    left: 0,
    margin: 60,
  },
  apiResponse: {
    marginHorizontal: 60,
  },
  statusCircle: {
    marginVertical: 60,
    alignItems: "center",
    justifyContent: "center",
    height: 250,
    width: 250,
    borderRadius: 175,
  },
});
