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

            <header className="page-header">
                <h1 className="page-title">Blocks</h1>
            </header>

            <form
                onSubmit={addBlock}
                className="form-panel"
            >

                <h2 className="form-panel__title">Add Block</h2>

                <div className="form-grid">
                    <input
                        type="text"
                        placeholder="Block Name"
                        value={name}
                        onChange={(e) =>
                            setName(e.target.value)
                        }
                        required
                        className="form-control"
                    />

                    <button
                        type="submit"
                        className="button"
                    >
                        Add Block
                    </button>
                </div>

            </form>

            <TableContainer title="Blocks">

                <table className="data-table">

                    <thead>

                        <tr>

                            <th>
                                Name
                            </th>

                            <th>
                                Actions
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {blocks.map(block => (

                            <tr key={block.id}>

                                <td>
                                    {block.name}
                                </td>

                                <td>

                                    <button
                                        onClick={() =>
                                            deleteBlock(block.id)
                                        }
                                        className="button button--danger"
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

export default BlocksPage;
