Desain Aplikasi
Aplikasi terdiri dari 2 komponen yang terprogram
	1. Messenger Client
	2. Messenger Server
Dalam komunikasi antar 2 komponen tersebut, digunakan RabbitMQ sebagai Message Broker. Setiap komponen memiliki message queue sendiri dengan id dirinya sendiri. Hal tersebut juga berlaku untuk server. Untuk komunikasi group, digunakan "exchange" untuk mengirim pesan ke semua queue yang terdaftar dalam exchange. Exchange tersebut diketahui oleh server. Segala komunikasi yang dilakukan (private chat atau group chat), pasti melewati server messenger. 
Untuk representasi group, user disimpan dalam database. Setiap program server di jalankan, data dari database akan diambil dan direpresentasikan lagi ke exchange, queue dan lain lain. Perintah khusus seperti membuat group, leave group, di kirimkan ke server oleh client dan di proses di server.

Petunjuk Instalasi
1. Buka 2 projek yang bersesuaian (Client dan Server)
2. Lakukan Build kepada 2 projek tersebut.
3. Ambil 2 file .jar dan jalankan keduanya(urutan tidak penting)

Cara Menjalankan Program 
1. Jalankan kedua file.jar dari hasil build (instalasi)
2. Masukkan command pada aplikasi client messenger
3. Tekan help untuk mendapatkan list command.

Tes


Langkah-langkah melakukan tes
1. Jalankan 2 program(client messenger dan server messenger)
2. Masukkan command yang diinginkan
3. Coba segala skenario yang memungkinkan (skenario benar dan salah, atau konten pesan yang salah maupun benar)
4. Cek database untuk command yang bersesuaian.
5. Lihat hasilnya dan bandingkan dengan yang seharusnya.