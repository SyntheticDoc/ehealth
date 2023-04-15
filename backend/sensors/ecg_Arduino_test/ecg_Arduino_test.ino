
#include <ArduinoJson.h>

DynamicJsonDocument output(1024);
JsonArray data = output.createNestedArray("data");

void add_values(JsonArray a , int len) {
  
  for (int i = 0; i <len; i++){
    a.add(0);
    }
  
  }
void clear_xValues(JsonArray a , int len) {
  
   for (int i = 0; i <len; i++){
    a[i] = 0;
    }
  
  
  }


// create array with a 100 zeros
//then set a value each milisecond 
//then send the array each second? 

void setup() {
  // Initialize Serial port
  Serial.begin(9600); 
  Serial.println("This is a Test. the following Text should be in Json syntax");
  Serial.println();

  pinMode(10, INPUT); // Setup for leads off detection LO +
  pinMode(11, INPUT); // Setup for leads off detection LO -

  // start filling ouput json 
 // output["Name"] = "SomeName"; 
 // output["time"] = "12:00"; 

 add_values(data,102); 

 serializeJson(output,Serial);
 
}

void loop() {

  // create array with a 100 zeros
  //then set a value each milisecond 
  //then send the array each second

  
  clear_xValues(data,100); 
  for (int i = 0; i<=100 ; i++){
    if((digitalRead(10) != 1) && (digitalRead(11) != 1)){
    int A0_read = analogRead(A0); // reads representation of voltage (0-3.3V mapped onto 0-1023 integer) from pin 

   // TO ADD Compute the actual voltage 
   //Serial.println(A0_read); 

    data[i] = A0_read;
    }

delay(1); // delays 1 millisecond 

}

serializeJson(output,Serial); // sends a json about every second 
Serial.println();
  

}
