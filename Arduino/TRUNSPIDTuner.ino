String string, p, i, d;

void setup() {
  // put your setup code here, to run once:

}

void loop() {
  // put your main code here, to run repeatedly:

}

void getData() {
  if(Serial.available()){
    string = "";
    while(Serial.available()){
      char comeIn = ((byte)Serial.read());
      if(comeIn == ':') {
        break;
      }else{
        string += comeIn;
      }
      delay(5);
    }
    if(string.startsWith("p")){
      p = string.substring(1);
    }
    else if(string.startsWith("i")){
      i = string.substring(1);
    }
    else if(string.startsWith("d")){
      d = string.substring(1);
    }
    Serial.print(p);
    Serial.print(" : ");
    Serial.print(i);
    Serial.print(" : ");
    Serial.println(d);
  }
}
