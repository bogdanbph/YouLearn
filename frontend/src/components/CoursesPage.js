import React from "react";
import NavigationBar from "./NavigationBar";
import '../styles/CoursesPage.css';

class CoursesPage extends React.Component {

    constructor(props) {
        super(props);
    }

    retrieveCourses = () => {

    }

    handleOnclick = (e) => {
        console.log(e);
        document.getElementById("java-vid").addEventListener('click', () => {
            console.log('hello');
            document.getElementById("java-vid").className = "fullScreen";
        })
    }

    render() {
        return (
            <div className="courses-page">
                <NavigationBar logout={this.props.logout}/>
                <div className="common-body" id="common-body-courses">
                    <ul className="list-unstyled">
                        <li>
                            <div className="card" style={{width:'18rem'}}>
                                <div className="content-card">
                                    <iframe onLoad={this.handleOnclick} id="java-vid" width="200" height="200" src="https://www.youtube.com/embed/eIrMbAQSU34" title="YouTube video player" frameBorder={0} allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowFullScreen></iframe>                        
                                    <p className="description-card">This course will drive you from beginner to expert in JAVA!</p>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        );
    }
}

export default CoursesPage;