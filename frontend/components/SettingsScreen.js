import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  Switch,
} from "react-native";
import React, { useEffect, useState, useContext } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-native-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons/faArrowLeft";
import { AppContext } from "../App";
import Toast from 'react-native-toast-message';
import { faEye } from '@fortawesome/free-solid-svg-icons';
import { faEyeSlash } from '@fortawesome/free-solid-svg-icons';

const textSize = 30;
const SettingsScreen = ({ navigation }) => {
  
  const { generaluser, setGeneraluser } = useContext(AppContext);
  const [name, setName] = useState(generaluser.name);
  const [address, setAddress] = useState(generaluser.address);
  const [phoneNumber, setPhoneNumber] = useState(generaluser.phone);
  const [email, setEmail] = useState("max.mustermann@example.com");
  const [emergency, setEmergency] = useState(generaluser.emergency);
  const [password, setPassword] = useState(generaluser.password); 
 
  const [newPassword, setNewPassword] = useState();
  const [newPassword1, setNewPassword1] = useState();
  const [showPassword, setShowPassword] = useState(false); 
  const [showPassword1, setShowPassword1] = useState(false); 


  const updateUser = async () => {

    if(newPassword!=newPassword1){
      Toast.show({
        type: 'error',
        text1: 'Passwörter sind nicht gleich',
        

      });
      return;
    }


    console.log(generaluser)
    const postData = {
			name: name,
      address: address,
      phone: phoneNumber,
      emergency: emergency,
			password: newPassword,
			devices: [],
      oldName: name,
      oldPassword:generaluser.password

		};
    console.log(postData)
    setPassword(postData.password); 
    console.log(password)
    
    const response = await fetch(
      "http://" +
        "128.131.193.44" +
        ":8080/user/update-user",
      {
        method: "Post",
        headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(postData),
      }
    );
    const json = await response.json();



	if(json.error!==undefined){
        Toast.show({
			type: 'error',
			text1: 'ERROR: Kontrolliere deine Handynummer!',
			style:{backgroundColor: "red"}
	
		  });
      console.log(error);
        console.log("FAIL")
      }else{
        setGeneraluser(json); 
        //
        Toast.show({
          type: 'success',
          text1: 'SAVED Successful',
          
  
        });
        navigation.navigate('Home');
    
      }


	console.log(json)
    setGeneraluser(json);
  };

  const handleSaveChanges = () => {    
    console.log(name, address, phoneNumber, emergency);
    updateUser();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Einstellungen </Text>

      <View style={styles.section}>
	  {generaluser!==null ? (
        <Text style={styles.name}>Name: {generaluser.name}</Text>
      ) : (
        <Text style={styles.sectionTitle}></Text>
      )}
        
    
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Adresse</Text>
        <TextInput
          style={styles.input}
          value={address}
          onChangeText={setAddress}
        />
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Handynummer (nur Zahlen)</Text>
        <TextInput
          style={styles.input}
          value={phoneNumber.toString()}
          onChangeText={setPhoneNumber}
          keyboardType="phone-pad"
        />
      </View>
      
      

      
{/* 
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>E-Mail-Adresse</Text>
        <TextInput
          style={styles.input}
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
        />
      </View>
	
	  <View style={styles.section}>
        <Text style={styles.sectionTitle}>Passwort</Text>
        <TextInput
          style={styles.input}
          value={password}
          onChangeText={setPassword}
        />
      </View>
*/}
      <View style={styles.checkbox}>
        <Switch value={emergency} onValueChange={setEmergency} />

        <Text style={styles.emergency}>
          Bei einem Herzstillstand möchte ich, dass die Rettung direkt
          verständigt wird
        </Text>
      </View>

      <Text style={styles.sectionTitle}>Passwort ändern</Text>
      <View style={styles.inputContainer}>
        
        <TextInput
          style={styles.inputpasswort}
          placeholder="Neues Passwort eingeben:"
          onChangeText={setNewPassword1}
          value={newPassword1}
          secureTextEntry={!showPassword}
        />
        <TouchableOpacity
          style={styles.eyeIcon}
          onPress={() => setShowPassword(!showPassword)}
        >
          <FontAwesomeIcon
            style={{ color: showPassword ? 'gray' : '#454545' }}
            icon={showPassword ? faEyeSlash : faEye}
            size={20}
          />
        </TouchableOpacity>
      </View>

      <View style={styles.inputContainer}>
        <TextInput
          style={styles.inputpasswort}
          placeholder="Neues Passwort wiederholden:"
          onChangeText={setNewPassword}
          value={newPassword}
          secureTextEntry={!showPassword1}
        />
        <TouchableOpacity
          style={styles.eyeIcon}
          onPress={() => setShowPassword1(!showPassword1)}
        >
          <FontAwesomeIcon
            style={{ color: showPassword1 ? 'gray' : '#454545' }}
            icon={showPassword1 ? faEyeSlash : faEye}
            size={20}
          />
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.button} onPress={handleSaveChanges}>
        <Text style={styles.buttonText}>Änderungen speichern</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={styles.backButton}
        onPress={() => {
          navigation.navigate("Home");
        }}
      >
        <FontAwesomeIcon
          icon={faArrowLeft}
          style={{ color: "#454545" }}
          size={50}
        ></FontAwesomeIcon>
      </TouchableOpacity>
    </View>
  );
};

export default SettingsScreen;

const styles = StyleSheet.create({
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: 'gray',
    backgroundColor: 'white',
    borderRadius: 5,
    padding: 10,
    marginBottom: 20,
    width: '100%',
  },
  inputpasswort: {
    flex: 1,
    marginRight: 10,
  },
  eyeIcon: {
    marginLeft: 10,
  },
  container: {
    flex: 1,
    backgroundColor: "#93CAED",
    padding: 20,
    marginTop: 40,
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 16,
    marginTop:10,
    color: "#454545",
  },
  section: {
    marginBottom: 20,
  },
  checkbox: {
    margin: 20,
    flexDirection: "row",
    alignItems: "center",
    color: "#454545",
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 10,
    color: "#454545",
  },
  name: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 0,
    color: "#454545",
  },

  buttonText: {
    fontSize: 20,
    fontWeight: "bold",
    color: "lightgray",
  },
  input: {
    borderWidth: 1,
    borderColor: "gray",
    backgroundColor: "white",
    borderRadius: 5,
    padding: 10,
    width: "100%",
    color: "#454545",
  },
  emergency: {
    fontSize: 14,
    color: "#454545",
    marginLeft: 10,
  },
  button: {
    backgroundColor: "#454545",
    borderRadius: 5,
    padding: 10,
    marginTop: 20,
    alignItems: "center",
    justifyContent: "center",
    borderColor: "#232323",
    borderWidth: 2,
  },
  backButton: {
    position: "absolute",
   top: 0,
    right: 0,
    margin: 35,
  },
});
