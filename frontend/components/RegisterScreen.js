import React, { useState, useContext } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  TextInput,
  Switch,
  Button,
  StyleSheet,
} from "react-native";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons/faArrowLeft";
import { FontAwesomeIcon } from "@fortawesome/react-native-fontawesome";
import { faHeartPulse } from "@fortawesome/free-solid-svg-icons/faHeartPulse";
import { castEmergencyCall, postUser } from "./NetworkFunctions";
import { AppContext } from "../App";

const RegisterScreen = ({ navigation }) => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");

  const { generaluser, setGeneraluser } = useContext(AppContext);


  const postUser = async () => {
    
    const response = await fetch(
      "http://" +
                "10.0.0.58" +
                ":8080/user/post-user?name=" +
                name +
                "&address=" +
                address +
                "&phone=" +
                phoneNumber +
                "&emergency=" +
                true +
                "&password=" +
                password,
      {
        method: 'Post',
      }

    );
    const json = await response.json();
    setGeneraluser(json); 
  
  }


  const handleRegister = () => {
    // Hier kannst du den Code schreiben, um die Registrierung durchzuführen
    console.log(name, email, address, phoneNumber);
  };

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
        <Text style={styles.heading}>Guard </Text>
      </View>
      <Text style={styles.subtitle}>Bitte registriere dich:</Text>
      <TextInput
        style={styles.input}
        placeholder="Name"
        onChangeText={setName}
        value={name}
      />
      
      <TextInput
        style={styles.input}
        placeholder="Adresse"
        onChangeText={setAddress}
        value={address}
      />
      <TextInput
        style={styles.input}
        placeholder="Handynummer"
        onChangeText={setPhoneNumber}
        value={phoneNumber}
        keyboardType="phone-pad"
      />
      <TextInput
        style={styles.input}
        placeholder="Passwort"
        onChangeText={setPassword}
        value={password}
      />
      <Text>
        Klicke{" "}
        <Text
          style={{ color: "blue", textDecorationLine: "underline" }}
          onPress={() => {
            navigation.navigate("Login");
          }}
        >
          hier
        </Text>{" "}
        um dich anzumelden.
      </Text>

      <TouchableOpacity
        style={styles.button}
        onPress={() => {
         postUser(); 
          navigation.navigate("Home");
        }}
      >
        <Text style={styles.buttonText}>Registrieren</Text>
      </TouchableOpacity>
    </View>
  );
};

export default RegisterScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#93CAED",
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
  },
  heading: {
    color: "white",
    fontSize: 50,
  },
  headerContainer: {
    marginTop: 20,
    marginBottom: 50,
    flexDirection: "row",
    alignItems: "center",
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 20,
    color: "#454545",
  },
  subtitle: {
    fontSize: 15,
    fontWeight: "bold",
    marginBottom: 20,
    color: "#454545",
  },
  input: {
    borderWidth: 1,
    borderColor: "gray",
    backgroundColor: "white",
    borderRadius: 5,
    padding: 10,
    marginBottom: 20,
    width: "100%",
  },
  arrow: {
    position: "absolute",
    bottom: 0,
    right: 0,
    margin: 60,
  },
  button: {
    margin: 20,
    backgroundColor: "#454545",
    padding: 10,
    borderRadius: 5,
    alignItems: "center",
    justifyContent: "center",
    borderColor: "#232323",
    borderWidth: 2,
  },
  buttonText: {
    fontSize: 20,
    color: "lightgray",
    paddingHorizontal: 10,
    fontWeight: "bold",
  },
});
