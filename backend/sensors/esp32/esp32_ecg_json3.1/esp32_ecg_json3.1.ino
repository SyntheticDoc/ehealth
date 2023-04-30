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
DynamicJsonDocument header(200);

const char* ntpServer = "pool.ntp.org";
const char* ssid ="Milkwalker";
const char* password = "Wherey0ul1ve&420$";

const long  gmtOffset_sec = 3600;
const int   daylightOffset_sec = 3600;

//Your Domain name with URL path or IP address with path
String serverName = "http://192.168.188.95:8080/user/test";


// the following variables are unsigned longs because the time, measured in
// milliseconds, will quickly become a bigger number than can be stored in an int.
unsigned long lastTime = 0;
// Timer set to 10 minutes (600000)
//unsigned long timerDelay = 600000;
// Set timer to 5 seconds (5000)
unsigned long milliseconds= 0;




void setup() {
  Serial.begin(9600);

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


   // Initialize Serial port
  Serial.begin(9600); 
  //Serial.println("This is a Test. the following Text should be in Json syntax");
  //Serial.println();

  pinMode(15, INPUT); // Setup for leads off detection LO +
  pinMode(16, INPUT); // Setup for leads off detection LO -

  // TODO get time from network
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  Serial.print("Set time");
  Serial.println(get_time_format()); 

  milliseconds = millis(); 

}

void loop() {
  //Send an HTTP POST request every 10 minutes
  //if ((millis() - lastTime) > timerDelay) {
    //Check WiFi connection status
    unsigned long currenttime=millis()-milliseconds;  //gets miliseconds (this is only a temporary estimate)

  if(WiFi.status()== WL_CONNECTED){

     String serialized_header; 
     String serialized_output; 
     String data_string; 



     output.clear(); 

    JsonArray data;

    header.clear(); 
    header["checksum"] = "0123456789"; 
    header["timestamp"] = get_time_format()+" :currentime"; 


    output["resourceType"] = "Observation"; 
    output["id"] = "ekg"; 
    output["status"] = "final"; 
    JsonArray cat_arr = output.createNestedArray("category"); 
    JsonObject category = cat_arr.createNestedObject();
    // //category["system"] = "http://terminology.hl7.org/CodeSystem/observation-category"; 
    // //category["code"] = "procedure"; 
    // //category["display"] = "Procedure"; 

    JsonObject device = output.createNestedObject("device"); 
    device["display"] = "ESP32_ECG_ELEC_POTL_I"; 

    JsonArray comp = output.createNestedArray("component");
    JsonObject component = comp.createNestedObject(); 
    component["code "] = "Still_to add Coding ";

    // // still to add value Sampledata etc.
    // //JsonObject sampleData = component.createNestedObject("valueSampledData"); 
    // // TODO add nested Obj. origin with value 



    component["interval" ] = "10";  //
    component["intervalunit"] = "ms";
    component["factor"]  = 1.612;
    component["dimensions"] = 1; 
    component["lowerlimit"] = -3300;
    component["upperlimit"] = 3300; 
    component["dimension"] = 1; 

    // //data = component.createNestedArray("data"); 



  
    int AO_voltage = 0; 
    //add_x_values(data,1000); 
  
    for (int i = 0; i<1000 ; i++){
    
      AO_voltage = calculate_voltage(get_analogRead(),4095); // 4095 is the max anlog read value of esp32
      
      data_string = data_string + " " + String(AO_voltage); 
      //data[i] = AO_voltage; 

      delay(1); // delays 1 millisecond 

    }
    component["data"] = data_string;
    serializeJson(header,serialized_header);
    serializeJson(output,serialized_output);


    WiFiClient client;
    HTTPClient http;
    
    // Your Domain name with URL path or IP address with path
    http.begin(client, serverName);
      
      // If you need Node-RED/server authentication, insert user and password below
      //http.setAuthorization("REPLACE_WITH_SERVER_USERNAME", "REPLACE_WITH_SERVER_PASSWORD");
      
      http.addHeader("Content-Type", "application/json");

      int httpResponseCode = http.POST(serialized_header+serialized_output);   
      String payload = "{}"; 


      if (httpResponseCode>0) {
        Serial.print("HTTP Response code: ");
        Serial.println(httpResponseCode);
        payload = http.getString();
      }
      
      // If you need an HTTP request with a content type: application/json, use the following:
      //http.addHeader("Content-Type", "application/json");
      //int httpResponseCode = http.POST("{\"api_key\":\"tPmAT5Ab3j7F9\",\"sensor\":\"BME280\",\"value1\":\"24.25\",\"value2\":\"49.54\",\"value3\":\"1005.14\"}");

      // If you need an HTTP request with a content type: text/plain
      //http.addHeader("Content-Type", "text/plain");
      //int httpResponseCode = http.POST("Hello, World!");
     
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);
      Serial.println(payload);
        
      // Free resources
      http.end();
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
   
    else {  return -1;  } //returns -1 if it is detected that the leads are off
  }

int calculate_voltage(int input, int max_value) {// calculates the actual values (mx for esp32 is 40
  long mv = ((input*1000)/max_value) *100; 

  return int(mv);   // in mv and scaled down 
  }

String get_time_format(){
  struct tm timeinfo;

  String time; 

  if(!getLocalTime(&timeinfo)){
    Serial.println("Failed to obtain time");
    return "an error occured ";
  }

  char hour[3]; 
  char min[3]; 
  char s[3]; 
  char year[5]; 
  char day[3];
  char month[3]; 
  // TODO: Month number; 

  strftime(hour,3, "%H", &timeinfo); // get hours in 12hour format 
  strftime(min,3, "%M", &timeinfo);
  strftime(s,3, "%S", &timeinfo);
  strftime(year,5, "%Y", &timeinfo);
  strftime(day,3, "%d", &timeinfo);
  month[0] = '0';
  month[1] = '3'; 

  time = String(day)+"."+String(month)+"."+String(year)+" "+String(hour)+":"+String(min)+":"+String(s);

  return time; 

}




