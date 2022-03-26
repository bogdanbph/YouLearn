import React, { useState } from "react";
import NavigationBar from "./NavigationBar";
import { toast } from 'react-toastify';
import CourseService from "../service/CourseService";

const YOUTUBE_PLAYLIST_ITEMS_API = "https://www.googleapis.com/youtube/v3/playlistItems";

export async function getPlaylist() {

}

class CoursePage extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            body: [],
            available: false
        }
    }

    async componentDidMount() {
        const playlistId = window.location.pathname.split('/')[2];
        await this.checkValidPlaylist(playlistId);
        if (this.state.available === true) {
            await this.renderData(playlistId);
        }
        else {
            toast.error("Course unavailable with id: " + playlistId, {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined
            });
            setTimeout(function() {
                window.location.href = '/courses'
            }, 2500);
        }
    }

    checkValidPlaylist = async (playlistId) => {
        await CourseService.checkValidPlaylist(playlistId, localStorage.getItem('token'))
        .then(res => {
            this.setState({
                available: res.data
            })
        })
        .catch(ex => {
            console.log(ex);
        })
    }

    renderData = async (playlistId) => {
        const apiKey = "AIzaSyB6qwQn6L_VW8PSINSFDuQSQrSboDv2PfA";
        await fetch(`${YOUTUBE_PLAYLIST_ITEMS_API}?part=snippet&maxResults=50&playlistId=${playlistId}&key=${apiKey}`)
        .then(async res => {
            const data = await res.json();

            this.setState({
                body: <main className="course-container">
                    <h1 className="course-title">
                        My playlist
                    </h1>

                    <ul className="chapters">
                        {data.items.map((item) => {
                            const { id, snippet = {} } = item;
                            const { title, thumbnails = {}, resourceId } = snippet;
                            const { medium = {} } = thumbnails;

                            return (
                                <li key={id} className="chapter-info">
                                    {/* ={`https://www.youtube.com/watch?v=${resourceId.videoId}`} */}
                                    <a>
                                        <h3>{ title }</h3>
                                        <p>
                                            <img width={medium.width} height={medium.height} src={medium.url} alt=""/>
                                            <p style={{display: 'inline-block'}}>In this chapter you</p>
                                        </p>
                                    </a>
                                </li>
                            )
                        })}
                    </ul>
                </main>
            });
        });
    }

    render() {

        return (
            <div className="common-body">
                <NavigationBar logout={this.props.logout}/>
                {this.state.body}
            </div>
        )
    }
}

export default CoursePage;
