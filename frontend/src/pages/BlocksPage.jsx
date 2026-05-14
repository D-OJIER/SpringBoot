import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";

function BlocksPage() {

    const [blocks, setBlocks] = useState([]);

    const [name, setName] = useState("");

    useEffect(() => {
        fetchBlocks();
    }, []);

    const fetchBlocks = async () => {

        try {

            const response =
                await api.get("/blocks");

            setBlocks(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    const addBlock = async (e) => {

        e.preventDefault();

        try {

            await api.post("/blocks", {
                name
            });

            setName("");

            fetchBlocks();

        } catch (error) {

            console.error(error);

            alert("Failed to add block");
        }
    };

    const deleteBlock = async (id) => {

        const confirmed =
            window.confirm(
                "Delete this block?"
            );

        if (!confirmed) return;

        try {

            await api.delete(`/blocks/${id}`);

            fetchBlocks();

        } catch (error) {

            console.error(error);

            alert("Failed to delete block");
        }
    };

    return (

        <MainLayout>

            <h1>Blocks</h1>

            <form
                onSubmit={addBlock}
                style={formStyle}
            >

                <h2>Add Block</h2>

                <input
                    type="text"
                    placeholder="Block Name"
                    value={name}
                    onChange={(e) =>
                        setName(e.target.value)
                    }
                    required
                    style={inputStyle}
                />

                <button
                    type="submit"
                    style={buttonStyle}
                >
                    Add Block
                </button>

            </form>

            <TableContainer title="Blocks">

                <table style={{
                    width: "100%",
                    borderCollapse: "collapse"
                }}>

                    <thead>

                        <tr>

                            <th style={thStyle}>
                                Name
                            </th>

                            <th style={thStyle}>
                                Actions
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {blocks.map(block => (

                            <tr key={block.id}>

                                <td style={tdStyle}>
                                    {block.name}
                                </td>

                                <td style={tdStyle}>

                                    <button
                                        onClick={() =>
                                            deleteBlock(block.id)
                                        }
                                        style={deleteButtonStyle}
                                    >
                                        Delete
                                    </button>

                                </td>

                            </tr>

                        ))}

                    </tbody>

                </table>

            </TableContainer>

        </MainLayout>
    );
}

const formStyle = {
    backgroundColor: "#1b1b1b",
    padding: "20px",
    borderRadius: "16px",
    marginBottom: "20px",
    border: "1px solid #333"
};

const inputStyle = {
    width: "100%",
    padding: "10px",
    marginBottom: "10px",
    borderRadius: "8px",
    border: "1px solid #444",
    backgroundColor: "#222",
    color: "white"
};

const buttonStyle = {
    padding: "10px 20px",
    borderRadius: "8px",
    border: "none",
    backgroundColor: "#2563eb",
    color: "white",
    cursor: "pointer"
};

const deleteButtonStyle = {
    backgroundColor: "#dc2626",
    color: "white",
    border: "none",
    padding: "8px 12px",
    borderRadius: "8px",
    cursor: "pointer"
};

const thStyle = {
    textAlign: "left",
    padding: "12px",
    borderBottom: "1px solid #333"
};

const tdStyle = {
    padding: "12px",
    borderBottom: "1px solid #222"
};

export default BlocksPage;