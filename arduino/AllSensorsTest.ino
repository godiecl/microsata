#include "DHT.h"
#define DHTPIN 4
#define DHTTYPE DHT22   // Sensor DHT22
DHT dht(DHTPIN, DHTTYPE);
volatile int NumPulsos; //variable para la cantidad de pulsos recibidos por el flujometro
volatile int NumPulsosPluv;//variable para la cantidad de pulsos recibidos por el pluviometro
int PinSensor = 2;    //Sensor conectado en el pin 2
int PinSensorP = 3;   //Pluviometro conectado en el pin 3
float factor_conversion=7.5; //para convertir de frecuencia a caudal
float factor_conversionP = 0.27945; //para convertir de mm de lluvia a litros de agua por metro cuadrado
float volumen=0;
float volumenAguaCaida= 0;
long dt=0; //variación de tiempo por cada bucle
long t0=0; //millis() del bucle anterior
//---Función que se ejecuta en interrupción---------------
void ContarPulsos ()
{
  NumPulsos++;   //incrementamos la variable de pulsos
}
void ContarPulsos2 ()
{
  NumPulsosPluv++;  //incrementamos la variable de pulsos del pluviometro
}

//---Función para obtener frecuencia de los pulsos del pluviometro--------
int ObtenerFrecuencia()
{
  int frecuencia;
  NumPulsosPluv = 0;   //Ponemos a 0 el número de pulsos
  interrupts();    //Habilitamos las interrupciones
  delay(1000);   //muestra de 1 segundo
  noInterrupts(); //Desabilitamos las interrupciones
  frecuencia=NumPulsos; //Hz(pulsos por segundo)
  return frecuencia;
}
//---Función para obtener frecuencia de los pulsos del pluviometro--------
int ObtenerFrecuencia2()
{
  int frecuencia2;
  NumPulsosPluv = 0;   //Ponemos a 0 el número de pulsos
  interrupts();    //Habilitamos las interrupciones
  delay(1000);   //muestra de 1 segundo
  noInterrupts(); //Desabilitamos las interrupciones
  frecuencia2=NumPulsosPluv; //Hz(pulsos por segundo)
  return frecuencia2;
}

void setup()
{
  Serial.begin(9600);
  delay(500);//Delay to let system boot
  delay(1000);//Wait before accessing Sensor
  dht.begin();
  pinMode(PinSensor, INPUT);
  pinMode(PinSensorP, INPUT);
  attachInterrupt(0,ContarPulsos,RISING); //(Interrupcion 0(Pin2),funcion,Flanco de subida)
  attachInterrupt(1,ContarPulsos2,RISING); //(Interrupcion 1(Pin3),funcion,Flanco de subida)
  Serial.println ("Envie 'r' para restablecer el volumen a 0 Litros");
  t0=millis();
}

void loop ()
{
  float h = dht.readHumidity(); //Leemos la Humedad
  float t = dht.readTemperature(); //Leemos la temperatura en grados Celsius
  //DHT.read11(dht_apin);
  Serial.println("|         ");
  Serial.print("Humedad: ");
  Serial.print(h);
  Serial.print("%  ");
  Serial.print("Temperatura: ");
  Serial.print(t);
  Serial.println("C  ");

  if (Serial.available()) {
    if(Serial.read()=='r')volumen=0;//restablecemos el volumen si recibimos 'r'
  }
  float frecuencia=ObtenerFrecuencia(); //obtenemos la Frecuencia de los pulsos en Hz
  float caudal_L_m=frecuencia/factor_conversion; //calculamos el caudal en L/m
  float caudal_L_h=caudal_L_m*60; //calculamos el caudal en L/h
  dt=millis()-t0; //calculamos la variación de tiempo
  t0=millis();
  volumen=volumen+(caudal_L_m/60)*(dt/1000); // volumen(L)=caudal(L/s)*tiempo(s)

  //-----Enviamos por el puerto serie---------------
  Serial.print ("FrecuenciaPulsos: ");
  Serial.print (frecuencia,0);
  Serial.print ("Hz\tCaudal: ");
  Serial.print (caudal_L_m,3);
  Serial.print (" L/m\t");
  Serial.print (caudal_L_h,3);
  Serial.println ("L/h\tVolumen: ");
  Serial.print (volumen,3);
  Serial.print(" L\tVolts: ");


  int sensorVal=analogRead(A2);
//Serial.print(" Sensor Value: ");
//Serial.print(sensorVal);
  float voltage = (sensorVal*5.0)/1024.0;
    //Serial.print("Volts: ");
    Serial.print(voltage);

  float pressure_pascal = (3.0*((float)voltage-0.47))*1000000.0;
  float pressure_bar = pressure_pascal/10e5;
    Serial.print(" Pressure = ");
    Serial.print(pressure_bar-0.08);
    Serial.println(" bars");
    //Serial.print(" Pressure = ");


  float frecuencia2=ObtenerFrecuencia2(); //obtenemos la Frecuencia del pluviometro de los pulsos en Hz
  float agua_caida_L_m2=frecuencia2*factor_conversionP; //calculamos el agua caida en L/m2
  Serial.print ("FrecuenciaPluvPulsos: ");
  Serial.print (frecuencia2,0);
  Serial.print ("Hz\tAgua caida: ");
  Serial.print (agua_caida_L_m2,3);
  Serial.print (" L/m2\t");

  Serial.println("         |");


  delay(2000);

}