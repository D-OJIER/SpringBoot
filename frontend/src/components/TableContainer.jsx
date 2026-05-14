function TableContainer({ title, children }) {

    return (

        <div style={{
            backgroundColor: "#1b1b1b",
            borderRadius: "16px",
            padding: "20px",
            border: "1px solid #333",
            marginTop: "20px"
        }}>

            <h2 style={{
                marginBottom: "20px"
            }}>
                {title}
            </h2>

            {children}

        </div>
    );
}

export default TableContainer;