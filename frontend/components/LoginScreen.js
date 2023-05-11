import React, { useState } from 'react';
import { View, Text,TouchableOpacity, TextInput, Switch, Button, StyleSheet } from 'react-native';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons/faArrowLeft';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faHeartPulse } from '@fortawesome/free-solid-svg-icons/faHeartPulse';


const LoginScreen = ({ navigation }) => {

    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
  
    const handleRegister = () => {
      // Hier kannst du den Code schreiben, um die Registrierung durchzuführen
      console.log(name, password);
    };
  
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
        <TextInput
          style={styles.input}
          placeholder="Passwort"
          onChangeText={setPassword}
          value={password}
        />
        <Text>
      Klicke{' '}
      <Text
        style={{ color: 'blue', textDecorationLine: 'underline' }}
        onPress={() => {
					navigation.navigate('Register');
                    handleRegister;
				}}
      >
        hier
      </Text>{' '}
      um dich zu registrieren.
    </Text>
        
        <TouchableOpacity
				style={styles.button}
				onPress={() => {
					navigation.navigate('Home');
                    handleRegister;
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
 
  
  
  
  
  
  