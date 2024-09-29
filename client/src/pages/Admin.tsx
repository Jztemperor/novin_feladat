import DataTable from "react-data-table-component";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { useState } from "react";
import { useEffect } from "react";
import { toast } from "react-toastify";
import Select from "react-select";

const options = [
  { value: "chocolate", label: "Chocolate" },
  { value: "strawberry", label: "Strawberry" },
  { value: "vanilla", label: "Vanilla" },
];

export const Admin = () => {
  const [data, setData] = useState([]);
  const [totalRows, setTotalRows] = useState();
  const [perPage, setPerPage] = useState(10);
  const [statePage, setStatePage] = useState(0);
  const [loading, setLoading] = useState(false);
  const { token } = useAuth();

  // Handle initial api call
  const getUsers = async (page: number, perPage: number) => {
    try {
      setLoading(true);
      const response: any = await axios.get(
        `http://localhost:8080/api/user?page=${page}&size=${perPage}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.data) {
        setData(response.data.content);
        setTotalRows(response.data.totalElements);
        setLoading(false);
      }
    } catch (errors: any) {
      toast.error(errors);
    }
  };

  // Delete user api call
  const handleDelete = async (userId: number) => {
    try {
      await axios.delete(`http://localhost:8080/api/user/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      toast.success("Felhasználó törölve!");
      getUsers(statePage, perPage);
    } catch (errors: any) {
      toast.error(errors);
    }
  };

  // Row change handler
  const handlePerRowsChange = async (newPerPage: number, page: number) => {
    getUsers(page - 1, newPerPage);
    setPerPage(newPerPage);
  };

  // Per page handler
  const handlePageChange = (page: number) => {
    setStatePage(page - 1);
    getUsers(page - 1, perPage);
  };

  // Initial api call
  useEffect(() => {
    getUsers(0, perPage);
  }, []);

  // Table column definitions
  const columns = [
    {
      name: "Felhasználónév",
      selector: (row: any) => row.username,
    },
    {
      name: "Szerepkör(ök)",
      cell: (row: any) => (
        <Select
          className="z-50"
          options={options}
          menuPortalTarget={document.body}
          styles={{
            menuPortal: (base) => ({
              ...base,
              zIndex: 9999,
            }),
          }}
        />
      ),
    },
    {
      name: "Törlés",
      selector: (row: any) => (
        <button
          onClick={() => handleDelete(row.id)}
          className="bg-red-500 text-white px-2 py-1 rounded"
        >
          Törlés
        </button>
      ),
    },
  ];

  return (
    <div className="mt-10 px-10">
      <DataTable
        title="Felhasználók"
        columns={columns}
        data={data}
        progressPending={loading}
        pagination
        paginationServer
        paginationTotalRows={totalRows}
        onChangeRowsPerPage={handlePerRowsChange}
        onChangePage={handlePageChange}
      ></DataTable>
    </div>
  );
};
