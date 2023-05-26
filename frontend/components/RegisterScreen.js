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
import Toast from 'react-native-toast-message';


const RegisterScreen = ({ navigation }) => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const [street, setStreet] = useState("");
  const [number, setNumber] = useState("");
  const[zip, setZip]= useState("");
  const[city, setCity]= useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [device, setDevice] = useState("");

  const { generaluser, setGeneraluser } = useContext(AppContext);

  const handleToast = ()  => {
    Toast.show({
      type: 'error',
      text1: 'Error',
      style:{backgroundColor: "red"}

    });
  };

  const postUser = async () => {

    const postData = {
			name: name,
      address: street +" "+number+", "+zip+" "+city,
      phone: phoneNumber,
      emergency: true,
			password: password,
			devices: [],
		};
    
    const response = await fetch(
      "http://" +
                "172.16.0.35" +
                ":8080/user/post-user"
               ,
      {
        method: 'Post',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(postData),
      }

    );
    const json = await response.json();
    if(json.error!==undefined){
      handleToast();
      console.log("FAIL")
    }else{
      setGeneraluser(json); 
      Toast.show({
        type: 'success',
        text1: 'REGISTRATION Successful',
        

      });
      navigation.navigate('Home');
  
    }
  }


  const handleRegister = () => {
    // Hier kannst du den Code schreiben, um die Registrierung durchzuführen
    console.log(name, address, phoneNumber);
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
        placeholder="Vorname und Nachname"
        onChangeText={setName}
        value={name}
      />
      
      <TextInput
        style={styles.input}
        placeholder="Straße"
        onChangeText={setStreet}
        value={street}
      />
      <TextInput
        style={styles.input}
        placeholder="Hausnummer"
        onChangeText={setNumber}
        value={number}
      />
      <TextInput
        style={styles.input}
        placeholder="Postleitzahl"
        onChangeText={setZip}
        value={zip}
      />
      <TextInput
        style={styles.input}
        placeholder="Ort"
        onChangeText={setCity}
        value={city}
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
