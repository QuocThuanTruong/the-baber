<br/>

<p align="center"><img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/logo.png" width="150"/></p>

# THE BARBER

<p align="left">
<img src="https://img.shields.io/badge/version-2.1.0-blue">
<img src="https://img.shields.io/badge/platforms-Android-orange.svg">
</p>

The barber is is an android application of a hair salon series, that helps people have an easy booking barber way for hair services. 
(including staff's application)

>  **Java** · **Firebase services** · **RxJava** · **Retrofit client** . **Room Database**

<br/>

# Technical stack

*   **Firebase services**
    - Authenication
    - Storage
    - Cloud Firestore
    - Firebase Cloud Messaging

*   **ReactiveX**
    - RxAndroid
    - RxJava

*   **Retrofit client**

*   **Local Databse**
    - Room Databse
    - Paper db

*   **Event bus**

# Authenication & Home Screen

Users only need Phone number to authentication and fill theirs information after first login.
Home screen includes 3 main activity of app: Booking, Cart and History. Beside, it also shows user booking information and some look book in server.

<div>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/auth_1.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/auth_2.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/auth_3.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/home.png" width="310"/>

</div>

<br/>


# Booking screen

Booking barber has 5 steps:
- Choosing salon.
- Choosing hair service.
- Choosing barber.
- Choosing appointment time.
- Checking information then confirm booking.
After that sending notification to staff app.

<div>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_1.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_2.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_3.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_31.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_4.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_5.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_6.png" width="310"/>
</div>

<br/>

### Staff app
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/book_7.png" width="310"/>
</div>

<br/>

# Shopping Screen & Cart Screen

Loading shopping items from Firestore and dividing into many categories. User adds items to Cart and adjusts quantity or removes items if needed.
Note: Shopping items that stored in Cart will be added to booking information when user books services.
Cart items are stored in local by using Room Database.

<div>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/cart_1.png" width="310"/>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/cart_2.png" width="310"/>
</div>

<br/>

# Booking history Screen

Including all user's booking history: time stamp, salon information, barber information and booked hair service.

<div>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/his_1.png" width="310"/>
</div>

<br/>

# Notification Screen

Containng all notification of app, notification with a ret dot in top-right corner represents unread status, staff can click to this notification to mark it as read.

<div>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/noti.png" width="310"/>
</div>

<br/>

# User profile Screen

Showing all user's information (rank is calculated by user's money has paid), including their look book, they can update their information by adjusting information and avatar then clicking update.

<div>
<img src="https://github.com/QuocThuanTruong/TheBarber/blob/master/Img/profile.png" width="310"/>
</div>

<br/>

# License

The Barber is available under the
[MIT license](https://opensource.org/licenses/MIT). See
[LICENSE](https://github.com/QuocThuanTruong/TheBarber/blob/master/LICENSE) for the full
license text.
