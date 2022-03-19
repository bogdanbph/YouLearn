import { Container, Nav, Navbar } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.css';
import '../styles/NavigationBar.css';
import brand from '../assets/brand.png';

const NavigationBar = (props) => {

    const handleLogout = e => {
        e.preventDefault();
        props.logout();
    }

    const handleLogin = () => {
        window.location.href = '/login';
    }

    return (
        <Navbar expand="lg" sticky="top" className="custom-navbar py-0">
            <Container>
                <Navbar.Brand href="/" className="brand">
                    You Learn <img alt="logo" src={brand} className="logo"/>
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="customNav">
                        
                            {localStorage.getItem('user') ? 
                                <>
                                    <Nav.Item href="/courses">
                                        <Nav.Link href="/courses">Courses</Nav.Link>    
                                    </Nav.Item>
                                    <Nav.Item href="/profile">
                                        <Nav.Link href='/profile'>Profile</Nav.Link>
                                    </Nav.Item>
                                    <Nav.Item href="/login">
                                        <Nav.Link onClick={handleLogout}>Logout</Nav.Link>
                                    </Nav.Item>
                                </>
                                :
                                <Nav.Item href="/login">
                                    <Nav.Link onClick={handleLogin}>Login</Nav.Link>
                                </Nav.Item>
                            }
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavigationBar;