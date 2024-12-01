# EyesOnMe - Describe the Picture for You

JanTune is a mobile-based application designed to detect heart diseases. In addition, it offers additional features such as providing recommendations for the nearest hospitals based on the user's location. By leveraging this application, we aim to bridge the gap in healthcare services and bring essential health information to the fingertips of all.

## Our project team members:

|  Student ID  | Name                         | Learning Path      | College                    |
|--------------|------------------------------|--------------------|----------------------------|
| M002B4KY0023 | Abdul Hafiz                  | Machine Learning   | Institut Teknologi Bandung |
| M002B4KY1659 | Habiburrohman                | Machine Learning   | Institut Teknologi Bandung |
| M002B4KY3008 | Muhammad Rafli Syahrullah    | Machine Learning   | Institut Teknologi Bandung |
| C547B4NY1898 | Ichlashul ‘Amal Santosa      | Cloud Computing    | UIN Sunan Gunung Djati     |
| C547B4NY2744 | Muhammad Arkan Raihan        | Cloud Computing    | UIN Sunan Gunung Djati     |
| A246B4KY0782 | Bagas Cahyawiguna            | Mobile Development | Universitas Kuningan       |
| A246B4KY2621 | Muhamad Alwind Maulana Yusuf | Mobile Development | Universitas Kuningan       |

# Documentation

API documentation for the Identification feature of the JanTune app, click [here](#).

## Additional Notes:
- Documentation for user login has not been created yet.
- To display the results, you also need to run the result identification feature in this directory: `JanTune/Cloud Computing/identification`
- Documentation for the result identification feature has not been provided yet. We apologize for this shortfall, but rest assured, we will complete it in the future.

## Steps to Replicate

These are the replication steps:

### Step 1: Clone the repository
```bash
git clone https://github.com/ariff-m/JanTune.git
cd JanTune
cd "Cloud Computing"
cd backend
```

### Step 2: Clone the repository
```bash
npm install bcrypt dotenv express jsonwebtoken multer mysql2 nodemon
```


## Step 3: Create the Database

To create the database, follow these steps:

1. Download the database.sql file from the Cloud Computing directory.
2. Execute the SQL commands in the downloaded `database.sql` file to set up the necessary database structure.

## Step 4: Configure the .env file

- Rename the env.example file to .env
- Edit the content of .env according to your configuration, for example:
```bash
PORT=8000 
DB_HOST=localhost 
DB_USERNAME=your_username 
DB_PASSWORD=your_password 
DB_NAME=tuanutebd 
JWT_SECRET=your_secret_key
```

## Step 5: Run the application
```bash
npm start
```

Open your browser and visit http://localhost:8000 in the terminal. If everything runs smoothly, you have successfully replicated this application.

## Step 6: Test the API

It is recommended to use Postman for testing. For documentation, refer to the instructions above.


