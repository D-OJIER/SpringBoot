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

            alert(error.response.data.message);
        }
    };

    return (

        <div className="login-page">

            <form
                onSubmit={handleLogin}
                className="card login-card"
            >

                <h1 className="login-title">
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
                    className="form-control"
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) =>
                        setPassword(e.target.value)
                    }
                    required
                    className="form-control"
                />

                <button
                    type="submit"
                    className="button"
                >
                    Login
                </button>

            </form>

        </div>
    );
}

export default LoginPage;
