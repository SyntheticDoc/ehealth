import {
	View,
	Text,
	StyleSheet,
	TouchableOpacity,
	TextInput,
	Switch
} from 'react-native';
import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons/faArrowLeft';

const textSize = 30;
const SettingsScreen = ({ navigation }) => {

	const [name, setName] = useState('Max Mustermann');
  const [address, setAddress] = useState('Musterstraße 1, 12345 Musterstadt');
  const [phoneNumber, setPhoneNumber] = useState('0123456789');
  const [email, setEmail] = useState('max.mustermann@example.com');
  const [emergency, setEmergency] = useState(false);

  const handleSaveChanges = () => {
    
    console.log(name, address, phoneNumber, email, emergency);
  };
	return (
		<View style={styles.container}>
      <Text style={styles.title}>Einstellungen</Text>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Name</Text>
        <TextInput
          style={styles.input}
          value={name}
          onChangeText={setName}
        />
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
        <Text style={styles.sectionTitle}>Handynummer</Text>
        <TextInput
          style={styles.input}
          value={phoneNumber}
          onChangeText={setPhoneNumber}
          keyboardType="phone-pad"
        />
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>E-Mail-Adresse</Text>
        <TextInput
          style={styles.input}
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
        />
      </View>

      <View style={styles.checkbox}>
        <Switch
          value={emergency}
          onValueChange={setEmergency}
        />
        
          <Text style={styles.emergency}>Bei einem Herzstillstand möchte ich, dass die Rettung direkt verständigt wird</Text>
        
      </View>

      <TouchableOpacity style={styles.button} onPress={handleSaveChanges}>
        <Text style={styles.buttonText}>Änderungen speichern</Text>
      </TouchableOpacity>
    <TouchableOpacity
				style={styles.backButton}
				onPress={() => {
					navigation.navigate('Home');
				}}
			>
				<FontAwesomeIcon
					icon={faArrowLeft}
					style={{ color: '#454545' }}
					size={50}
				></FontAwesomeIcon>
			</TouchableOpacity>
			</View>
			
		
	);
};

export default SettingsScreen;

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: '#93CAED',
		padding: 20,
		marginTop:40
	  },
	  title: {
		fontSize: 20,
		fontWeight: 'bold',
		marginBottom: 20,
		color:'#454545'
	  },
	  section: {
		marginBottom: 20,

	  },
	  checkbox: {
		margin: 20,
		flexDirection: 'row',
		alignItems: 'center',
		color:'#454545'
		
	  },
	  sectionTitle: {
		fontSize: 16,
		fontWeight: 'bold',
		marginBottom: 10,
		color:'#454545',
	  },
	  
	  buttonText: {
		fontSize: 20,
		fontWeight: 'bold',
		color: 'lightgray',
	  },
	  input: {
		borderWidth: 1,
		borderColor: 'gray',
		backgroundColor:'white',
		borderRadius: 5,
		padding: 10,
		width: '100%',
		color:'#454545'
	  },
	  emergency: {
		fontSize: 14,
		color:'#454545',
		marginLeft: 10,
	  },
	  button: {
		backgroundColor: '#454545',
		borderRadius: 5,
		padding: 10,
		marginTop: 20,
		alignItems: 'center',
		justifyContent: 'center', 
		borderColor:'#232323',
		borderWidth:2

	  },
	backButton: {
		position: 'absolute',
		bottom: 0,
		right: 0,
		margin: 60,

	},
	
});
