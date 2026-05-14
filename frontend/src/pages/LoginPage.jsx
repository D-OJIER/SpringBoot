import { useState } from "react";

import api from "../api/axios";

function LoginPage() {

    const [username, setUsername] = useState("");

    const [password, setPassword] = useState("");

    const handleLogin = async (e) => {

        e.preventDefault();

        try {

            const response =
                await api.post(
                    "/auth/login",
                    {
                        username,
                        password
                    }
                );

            localStorage.setItem(
                "token",
                response.data.token
            );

            window.location.href = "/";

        } catch (error) {

            console.error(error);

            alert("Invalid credentials");
        }
    };

    return (

        <div style={{
            minHeight: "100vh",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            backgroundColor: "#111"
        }}>

            <form
                onSubmit={handleLogin}
                style={{
                    backgroundColor: "#1b1b1b",
                    padding: "30px",
                    borderRadius: "16px",
                    width: "350px"
                }}
            >

                <h1 style={{
                    color: "white",
                    marginBottom: "20px"
                }}>
                    Login
                </h1>

                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) =>
                        setUsername(e.target.value)
                    }
                    required
                    style={inputStyle}
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) =>
                        setPassword(e.target.value)
                    }
                    required
                    style={inputStyle}
                />

                <button
                    type="submit"
                    style={buttonStyle}
                >
                    Login
                </button>

            </form>

        </div>
    );
}

const inputStyle = {
    width: "100%",
    padding: "12px",
    marginBottom: "12px",
    borderRadius: "8px",
    border: "1px solid #444",
    backgroundColor: "#222",
    color: "white"
};

const buttonStyle = {
    width: "100%",
    padding: "12px",
    borderRadius: "8px",
    border: "none",
    backgroundColor: "#2563eb",
    color: "white",
    cursor: "pointer"
};

export default LoginPage;