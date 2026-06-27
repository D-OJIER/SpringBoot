function TableContainer({ title, children }) {
  return (
    <div className="table-panel">
      <h2 className="table-panel__title">{title}</h2>

      <div className="table-scroll">{children}</div>
    </div>
  );
}

export default TableContainer;
