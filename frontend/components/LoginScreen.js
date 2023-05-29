import React, { useState, useContext } from 'react';
import { View, Text,TouchableOpacity, TextInput, Switch, Button, StyleSheet } from 'react-native';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons/faArrowLeft';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faHeartPulse } from '@fortawesome/free-solid-svg-icons/faHeartPulse';
import { AppContext } from "../App";
import Toast from 'react-native-toast-message';
import { faEye } from '@fortawesome/free-solid-svg-icons';
import { faEyeSlash } from '@fortawesome/free-solid-svg-icons';





const LoginScreen = ({ navigation }) => {

    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false); 

    
    const { generaluser, setGeneraluser } = useContext(AppContext);
  
    const handleRegister = () => {
      // Hier kannst du den Code schreiben, um die Registrierung durchzuführen
      console.log(name, password);
    };
    const handleToast = ()  => {
      Toast.show({
        type: 'error',
        text1: 'USER NOT FOUND',
        style:{backgroundColor: "red"}

      });
    };
    const getuser = async () => {
    
      if(name ===""){
        Toast.show({
          type: 'error',
          text1: 'Bitte gib deinen Namen an',
        });
        return;
      }
      if(password ===""){
        Toast.show({
          type: 'error',
          text1: 'Bitte gib dein Passwort an',
        });
        return;
      }

      const response = await fetch(
        "http://" +
                  "172.16.0.10" +
                  ":8080/user/get-user?name=" +
                  name +
                  "&password=" +
                  password, 
        {
          method: 'Get',
        }
  
      );
      const json = await response.json();
      console.log(json)
      if(json.error!==undefined){
        handleToast();
        console.log("FAIL")
      }else{
        setGeneraluser(json); 
        const user = generaluser; 
        user.password = password; 
        setGeneraluser(user)
       
        
        Toast.show({
          type: 'success',
          text1: 'LOGIN Successful',
          
  
        });
        navigation.navigate('Home');
    
      }
      
    }
  
    return(<View style={styles.container}>
        <View style={styles.headerContainer}>
        <FontAwesomeIcon
					style={{
						color: '#fff',
					}}
					icon={faHeartPulse}
					size={50}
				/>
                <Text style={styles.heading}>Guard </Text>
				
			</View>
        <Text style={styles.subtitle}>Bitte melde dich an:</Text>
        <TextInput
          style={styles.input}
          placeholder="Name"
          onChangeText={setName}
          value={name}
        />
        <View style={styles.inputContainer}>
        <TextInput
          style={styles.inputpasswort}
          placeholder="Passwort"
          onChangeText={setPassword}
          value={password}
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
        <Text>
      Klicke{' '}
      <Text
        style={{ color: 'blue', textDecorationLine: 'underline' }}
        onPress={() => {
					navigation.navigate('Register');
                  
				}}
      >
        hier
      </Text>{' '}
      um dich zu registrieren.
    </Text>
        
        <TouchableOpacity
				style={styles.button}
				onPress={() => {
					//navigation.navigate('Home');
           getuser();
				}}
			>
				 <Text style={styles.buttonText}>Anmelden</Text>
        
			</TouchableOpacity>

      </View>);
};

export default LoginScreen;

const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: '#93CAED',
      justifyContent: 'center',
      alignItems: 'center',
      padding: 20,
    },
    heading: {
		color: 'white',
		fontSize: 50,
	},
	headerContainer: {
		marginTop: 20,
        marginBottom: 50,
		flexDirection: 'row',
		alignItems: 'center',
	},
    title: {
      fontSize: 20,
      fontWeight: 'bold',
      marginBottom: 20,
      color:'#454545'
    },
    subtitle: {
        fontSize: 15,
        fontWeight: 'bold',
        marginBottom: 20,
        color:'#454545'
      },
    input: {
      borderWidth: 1,
      borderColor: 'gray',
      backgroundColor:'white',
      borderRadius: 5,
      padding: 10,
      marginBottom: 20,
      width: '100%',
    },
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
    arrow: {
		position: 'absolute',
		bottom: 0,
		right: 0,
		margin: 60,
	},
    button:{
        margin:20,
        backgroundColor:'#454545',
        padding: 10,
        borderRadius: 5,
		alignItems: 'center',
		justifyContent: 'center', 
		borderColor:'#232323',
		borderWidth:2
    }, 
    buttonText:{
        fontSize: 20,
        color: 'lightgray', 
        paddingHorizontal: 10,
        fontWeight: 'bold',
    }
  });
 
  
  
  
  
  
  