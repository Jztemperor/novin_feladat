import DataTable from "react-data-table-component";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { useState } from "react";
import { useEffect } from "react";
import { toast } from "react-toastify";
import Select, { MultiValue } from "react-select";
import { Authority } from "../types/Admin/Authority";
import { User } from "../types/Admin/User";
import { SelectedRoles } from "../types/Admin/SelectedRoles";
import { handleApiErrors } from "../util/errorUtil";

export const Admin = () => {
  const [data, setData] = useState([]);
  const [authorities, setAuthorities] = useState([]);
  const [totalRows, setTotalRows] = useState();
  const [perPage, setPerPage] = useState(10);
  const [statePage, setStatePage] = useState(0);
  const [loading, setLoading] = useState(false);
  const { token } = useAuth();
  const [selectedRoles, setSelectedRoles] = useState<SelectedRoles>({});

  // Initial api call to get available roles
  const getRoles = async () => {
    try {
      setLoading(true);
      const response: any = await axios.get(`http://localhost:8080/api/role`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response) {
        setLoading(false);
        setAuthorities(response.data);
      }
    } catch (errors: any) {
      Object.keys(errors.response.data.errors).forEach((key) => {
        toast.error(errors.response.data.errors[key]);
      });
    }
  };

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

        // Set initial role
        const initializedRoles: SelectedRoles = {};
        response.data.content.forEach((user: User) => {
          initializedRoles[user.id] = user.authorities.map((role) => ({
            value: role.roleId,
            label: role.authority,
          }));
        });

        setSelectedRoles(initializedRoles);
        setLoading(false);
      }
    } catch (errors: any) {
      handleApiErrors(errors);
    }
  };

  // Handle role change
  const handleRoleChange = (
    selected: MultiValue<{ value: number; label: string }>,
    userId: number
  ) => {
    // Cast selected to the desired type
    setSelectedRoles((prev) => ({
      ...prev,
      [userId]: selected as { value: number; label: string }[], // Cast to the correct type
    }));
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
      handleApiErrors(errors);
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

  const handleRoleSave = async (userId: number) => {
    const requestData = {
      userId: userId,
      roleIds: selectedRoles[userId].map((role) => role.value),
    };

    try {
      await axios.put("http://localhost:8080/api/user", requestData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      toast.success("Felhasználó sikeresen frissítve!");
    } catch (errors: any) {
      handleApiErrors(errors);
    }
  };

  // Maps Authorities from the API call to options for the multiselect
  const mapAuthoritiesToOptions = (authorities: Authority[]) => {
    return authorities.map(({ roleId, authority }) => ({
      value: roleId,
      label: authority,
    }));
  };

  // Do mapping
  const options = mapAuthoritiesToOptions(authorities);

  // Initial api call
  useEffect(() => {
    getUsers(0, perPage);
    getRoles();
  }, []);

  // Table column definitions
  const columns = [
    {
      name: "Felhasználónév",
      selector: (row: any) => row.username,
    },
    {
      name: "Szerepkör(ök)",
      cell: (row: User) => {
        // Filter out roles already assigned to the user
        const currentRoles = selectedRoles[row.id] || [];
        const availableOptions = options.filter(
          (option) => !currentRoles.some((role) => role.value === option.value)
        );

        return (
          <Select
            className="mr-5"
            options={availableOptions} // Use filtered options
            menuPortalTarget={document.body}
            isMulti
            value={currentRoles} // Show current roles
            onChange={(selected) => handleRoleChange(selected, row.id)} // Update state on change
            styles={{
              menuPortal: (base) => ({
                ...base,
                zIndex: 9999,
              }),
              option: (provided) => ({
                ...provided,
                fontSize: "12px",
              }),
              control: (provided) => ({
                ...provided,
                fontSize: "14px",
              }),
            }}
          />
        );
      },
    },

    {
      name: "Mentés",
      selector: (row: any) => {
        return (
          <button
            onClick={() => handleRoleSave(row.id)}
            className="bg-indigo-600 text-white font-bold py-2 px-4 rounded text-xs"
          >
            Mentés
          </button>
        );
      },
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
        noDataComponent={<div>Nincsenek megjeleníthető adatok</div>}
        paginationComponentOptions={{
          rowsPerPageText: "Sorok száma oldalanként:",
          rangeSeparatorText: "a",
        }}
      ></DataTable>
    </div>
  );
};
