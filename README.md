
The problem about this project cannot use repository as singleton because the connection is not stable. In this project we are using activity .

In ViewModel already extends library from Nordic. All service , connection state is monitor in viewmodel.  Only Rx and Tx in the repository.

Link problem in nordic devzone

https://devzone.nordicsemi.com/f/nordic-q-a/56954/android-nrf-blinky-error-is-e-blemanager-onmtuchanged-error-what-it-means-mtuchanged-error-and-the-status-number

# nRF BLINKY

Fork of nrf blinky from nordic .
Change blinky to UART 
BLE MVVM.

## SERVICE & CHARACTERISTIC
Just copy from UART sample UUID


## Demonstrate
MVVM
Viewmodel - BLE Manager
View - Blinky Activity

## Data Transmission

Send & Receive Success
Default packet 244 bytes





