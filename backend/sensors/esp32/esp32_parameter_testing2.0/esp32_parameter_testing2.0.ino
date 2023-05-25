/*
  Rui Santos
  Complete project details at Complete project details at https://RandomNerdTutorials.com/esp32-http-get-post-arduino/

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files.

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.
*/

#include <WiFi.h>  
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include "time.h"
#include <NTPClient.h>
#include <WiFiUdp.h> // to get the first time stamp for the beginning 

DynamicJsonDocument output(2980); //with capacity > 1040; file is always null (on arduino) or other unexpected behavior
//DynamicJsonDocument header(200);

const char* ntpServer = "pool.ntp.org";
const char* ssid ="A1-653BBD";
const char* password = "E2F613F91B";

const long  gmtOffset_sec = 3600;
const int   daylightOffset_sec = 3600;

//Your Domain name with URL path or IP address with path
String serverName = "http://10.0.0.56:8080/connect/registerECGDevice";


// the following variables are unsigned longs because the time, measured in
// milliseconds, will quickly become a bigger number than can be stored in an int.
unsigned long lastTime = 0;
// Timer set to 10 minutes (600000)
//unsigned long timerDelay = 600000;
// Set timer to 5 seconds (5000)
unsigned long milliseconds= 0;

String device_identifier = "0"; 

int sampling_rate = 1; 

bool in_miliV = true;




void setup() {
  Serial.begin(115200);

  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
 
  Serial.println("Timer set to 5 seconds (timerDelay variable), it will take 5 seconds before publishing the first reading.");
  delay(1000); 
  


   
  //Serial.println("This is a Test. the following Text should be in Json syntax");
  //Serial.println();

  pinMode(15, INPUT); // Setup for leads off detection LO +
  pinMode(16, INPUT); // Setup for leads off detection LO -

  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer); // gets the time from the network 
  Serial.println("Set time");
  Serial.println(get_time_format()); 

  delay(5000);

  device_identifier = get_identifier(register_device()); 

  milliseconds = millis(); 
  serverName = "http://10.0.0.56:8080/data/receive/esp32_custom";

  sampling_rate = get_sampling_rate(); 
  in_miliV = in_mV(); 

}

void loop() {

  //Send an HTTP POST request every 10 minutes
  //if ((millis() - lastTime) > timerDelay) {
    //Check WiFi connection status
    unsigned long currenttime = millis()-milliseconds;  

  if(WiFi.status()== WL_CONNECTED){

     String serialized_header; 
     String serialized_output; 
     String data_string; 



     output.clear(); 

    JsonArray data;

    output["identifier"] = device_identifier; 
    
    output["timestamp"] = get_time_format()+"."+currenttime;


    // //data = component.createNestedArray("data"); 



  
    double AO_voltage = 0; 
    //add_x_values(data,1000); 

    bool leadsOff = false ; //
  
    for (int i = 0; i<1000 ; i = i+sampling_rate){ 
    
      AO_voltage = calculate_voltage(get_analogRead(),4095); // 4095 is the max anlog read value of esp

      if(AO_voltage == 0 ){leadsOff = true;}
      
      if(i!=0){data_string = data_string + " ";}

      Serial.println("ECG_value :  "+String(AO_voltage)); 
      
      data_string = data_string + " " + String(AO_voltage); 
      //data[i] = AO_voltage; 

      delay(sampling_rate); // delays 1 millisecond 

    }
    output["data"] = data_string;
    output["leadsOff"] = leadsOff; 
    output["sampling"] = frequency(sampling_rate);
    serializeJson(output,serialized_output);


    WiFiClient client;
    HTTPClient http;
    
    // Your Domain name with URL path or IP address with path
    http.begin(client, serverName);
      
      // If you need Node-RED/server authentication, insert user and password below
      //http.setAuthorization("REPLACE_WITH_SERVER_USERNAME", "REPLACE_WITH_SERVER_PASSWORD");
      
      http.addHeader("Content-Type", "application/json");

      //register_device(); 

      int httpResponseCode = http.POST(serialized_output);   
      String payload = "{}"; 


    
      milliseconds = millis(); 
    }
    else {
      Serial.println("WiFi Disconnected");
    }
    //lastTime = millis();
  //}
}


int get_analogRead() { // gets the ecg output from the AD8232 (as int representation) 

  if((digitalRead(16) != 1) && (digitalRead(15) != 1)){
       return analogRead(14); 
   }
   
    else {  return 0;  } //returns 0 if leads are off 
  }

double calculate_voltage(int input, int max_value) {// calculates the actual values (max anolog read for esp is 4095) 
  double mv = double(input)/double(max_value) ;
  mv = (mv*10); // scaled for max of 10 mV

  return double(mv);   // in mv and scaled down 
  }


String get_time_format(){
  struct tm timeinfo;

  String time; 

  if(!getLocalTime(&timeinfo)){
    Serial.println("Failed to obtain time");
    return "an error occured ";
  }
  
  char formatted_time[50]; 
  int r ; 

  r = strftime(formatted_time,50,"%Y-05-%dT%H:%M:%S",&timeinfo); 
  //returns time in the format :  2023-04-27T18:17:51

  time = String(formatted_time);

  return time; 

}

String  register_device(){
DynamicJsonDocument register_json(1024);
register_json["id"] = 0; 
register_json["selfID"] = "3cfa4e0f7c6b5f2"; 
register_json["identifier"] = ""; 
register_json["name"] = "ESP32 custom ecg device"; 
register_json["leads"] = 1; 
JsonArray components = register_json.createNestedArray("components"); 
JsonObject comp1 = components.createNestedObject(); 
comp1["id"] =0;
comp1["selfID"] ="2708a274f35b1b"; 
comp1["identifier"] ="";
comp1["name"] ="ESP32_ECG_ELEC_POTL_I";

String serialized_json; 
serializeJson(register_json,serialized_json); 
Serial.println(serialized_json); 

 WiFiClient client;
 HTTPClient http;
http.begin(client, serverName);

http.addHeader("Content-Type", "application/json");

 int httpResponseCode = http.POST(serialized_json);   
  String payload = "{}"; 


  if (httpResponseCode>0) {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    payload = http.getString();
    Serial.println(payload); 
  }

  return payload; 
}

double frequency(int rate){
  return 1/(double(rate)/1000); 

}


String get_identifier(String json_str){
  String result; 

  int count = 0; 

  for (int i=0;i< json_str.length();i++  ){

    if ((count==3 )&& (json_str[i]!= '"')){
       result = result+json_str[i]; 
      
    }
    if (json_str[i] == '"'){count = count+1;}

  }delay(500);
  Serial.println( "recieved_identifier: " + result );

  return result; 

}

int get_sampling_rate(){
Serial.println("Please choose  a sampling rate :"); 
while (Serial.available() == 0) {   }                                 
  int input; 
  input = Serial.parseInt();                                             
  char mist = Serial.read();                                                  // eliminate read-in of 0 from control character by timeout
  if ((input < 0) || (input > 100)) {                        
   Serial.print("you entered something below zero or something out of optimal range");
   Serial.println("Please chose something between 1 and 100 ms ");
  }                                         
                                                       
  else {
    Serial.println("Sampling Rate set to: "+ String(input)+"ms"); 
    return input; 

  }    
  

}

bool in_mV(){
  Serial.println("Please choose if the output sould be in mV, 1 for yes and any other value for no: "); 
  while (Serial.available() == 0) {}                                     
  int input; 
  input = Serial.parseInt();                                              // read integer value from serial monitor
  char mist = Serial.read();                                                  // eliminate read-in of 0 from control character by timeout
  if ((input !=1)) {                            // check if PWM 0 is out of usable range
   Serial.println("you entered something other than 1");
   Serial.println("The results will be sent as plain analog input (int value)");
   return (false); 
  }                                         
                                                       
  else {
    Serial.println("The the sent values will be computed to milliVolt" ); 
    return true; 

  }    

}





