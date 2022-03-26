import { render } from "@testing-library/react";
import React, { useState } from "react";
import styles from '../styles/CoursePage.css';
import NavigationBar from "./NavigationBar";

const YOUTUBE_PLAYLIST_ITEMS_API = "https://www.googleapis.com/youtube/v3/playlistItems";

export async function getPlaylist() {

}

class CoursePage extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            body: []
        }
    }

    componentDidMount() {
        this.renderData();
    }

    renderData = async () => {
        const playlistId = "PLBlnK6fEyqRjKA_NuK9mHmlk0dZzuP1P5";
        const apiKey = "AIzaSyB6qwQn6L_VW8PSINSFDuQSQrSboDv2PfA";
        await fetch(`${YOUTUBE_PLAYLIST_ITEMS_API}?part=snippet&maxResults=50&playlistId=${playlistId}&key=${apiKey}`)
        .then(async res => {
            const data = await res.json();
            console.log(await data);

            this.setState({
                body: <main className="course-container">
                    <h1 className="course-title">
                        My playlist
                    </h1>

                    <ul className="chapters">
                        {data.items.map((item) => {
                            console.log(item);
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
