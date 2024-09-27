import DataTable from "react-data-table-component";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { useState } from "react";
import { useEffect } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

const columns = [
  {
    name: "Vásárló",
    selector: (row: any) => row.customerName,
  },
  {
    name: "Kiállítás Dátuma",
    selector: (row: any) => row.issueDate,
  },
  {
    name: "Esedékesség Dátuma",
    selector: (row: any) => row.dueDate,
  },
  {
    name: "Komment",
    selector: (row: any) => row.comment,
  },
  {
    name: "Ár",
    selector: (row: any) => row.totalPrice,
  },
];

export const Invoices = () => {
  const [data, setData] = useState([]);
  const [totalRows, setTotalRows] = useState();
  const [loading, setLoading] = useState(false);
  const { token, authorities } = useAuth();
  const [perPage, setPerPage] = useState(10);
  const navigate = useNavigate();

  const handlePageChange = (page: number) => {
    getInvoices(page - 1, perPage);
  };

  const handlePerRowsChange = async (newPerPage: number, page: number) => {
    try {
      setLoading(true);
      const response: any = await axios.get(
        `http://localhost:8080/api/invoice?page=${
          page - 1
        }&size=${newPerPage}&sort=issueDate,asc`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response) {
        setData(response.data.content);
        setTotalRows(response.data.totalElements);
        setPerPage(newPerPage);
        setLoading(false);
      }
    } catch (errors: any) {
      toast.error(errors);
    }
  };

  const getInvoices = async (page: number, perPage: number) => {
    try {
      setLoading(true);
      const response: any = await axios.get(
        `http://localhost:8080/api/invoice?page=${page}&size=${perPage}&sort=issueDate,asc`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response) {
        setData(response.data.content);
        setTotalRows(response.data.totalElements);
        setLoading(false);
      }
    } catch (errors: any) {
      toast.error(errors);
    }
  };

  useEffect(() => {
    getInvoices(0, perPage);
  }, []);

  const handleRowClick = (row: any) => {
    navigate(`/szamla/${row.id}`);
  };

  const CustomDataTableTitle = () => {
    return (
      <div className="flex justify-between items-center">
        <h2>Számlák</h2>

        {token &&
          (authorities.includes("Konyvelo") ||
            authorities.includes("Adminisztrator")) && (
            <Link
              to={"/szamla-letrehoz"}
              relative="path"
              className="bg-indigo-600 text-white font-bold py-2 px-4 rounded text-xs"
            >
              Számla létrehozás
            </Link>
          )}
      </div>
    );
  };

  return (
    <div className="mt-10 px-10">
      <DataTable
        title={<CustomDataTableTitle />}
        columns={columns}
        data={data}
        progressPending={loading}
        pagination
        paginationServer
        paginationTotalRows={totalRows}
        onChangeRowsPerPage={handlePerRowsChange}
        onChangePage={handlePageChange}
        onRowClicked={handleRowClick}
      ></DataTable>
    </div>
  );
};
