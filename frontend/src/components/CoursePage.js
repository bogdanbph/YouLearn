import React, { useState } from "react";
import NavigationBar from "./NavigationBar";
import { toast } from "react-toastify";
import CourseService from "../service/CourseService";
import "../styles/CoursePage.css";

const YOUTUBE_PLAYLIST_ITEMS_API =
  "https://www.googleapis.com/youtube/v3/playlistItems";

export async function getPlaylist() {}

class CoursePage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      body: [],
      available: false,
    };
  }

  async componentDidMount() {
    const playlistUrl = window.location.pathname.split("/")[2];
    console.log(playlistUrl);
    const playlistId = playlistUrl.split("=")[1].split("&")[0];
    const playlistName = playlistUrl.split("=")[2].replace("-", " ");

    await this.checkValidPlaylist(playlistId);
    if (this.state.available === true) {
      await this.renderData(playlistId, playlistName);
    } else {
      toast.error("Course unavailable with id: " + playlistId, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });
      setTimeout(function () {
        window.location.href = "/courses";
      }, 2500);
    }
  }

  checkValidPlaylist = async (playlistId) => {
    await CourseService.checkValidPlaylist(
      playlistId,
      localStorage.getItem("token")
    )
      .then((res) => {
        this.setState({
          available: res.data,
        });
      })
      .catch((ex) => {
        console.log(ex);
      });
  };

  renderData = async (playlistId, playlistName) => {
    await fetch(
      `${YOUTUBE_PLAYLIST_ITEMS_API}?part=snippet&maxResults=50&playlistId=${playlistId}&key=${process.env.REACT_APP_API_YOUTUBE}`
    ).then(async (res) => {
      const data = await res.json();
      console.log(data);
      this.setState({
        body: (
          <main className="course-container">
            <h1 className="course-title" style={{ marginLeft: "10%" }}>
              {playlistName}
            </h1>

            <ul className="chapters">
              {data?.items?.map((item) => {
                const { id, snippet = {} } = item;
                const {
                  title,
                  thumbnails = {},
                  resourceId,
                  description,
                } = snippet;
                const { medium = {} } = thumbnails;

                const shortDescription = description.substring(0, 250);
                const dots = "...";
                const restOfDescription = description.substring(250);

                return (
                  <li key={id} className="chapter-info">
                    {/* ={`https://www.youtube.com/watch?v=${resourceId.videoId}`} */}
                    <button
                      className="mark-complete"
                      style={{
                        float: "right",
                        marginRight: "20%",
                        marginTop: "5%",
                      }}
                      onClick={() =>
                        this.handleCompleteChapter(resourceId.videoId)
                      }
                    >
                      Mark as Complete
                    </button>
                    <a
                      href={`https://www.youtube.com/watch?v=${resourceId.videoId}`}
                    >
                      <h3>{title}</h3>
                      <p>
                        <img
                          width={medium.width}
                          height={medium.height}
                          src={medium.url}
                          alt=""
                        />
                      </p>
                    </a>
                    <p style={{ display: "inline-block" }}>
                      {shortDescription}
                      <span
                        id={"dots" + id}
                        onClick={() => this.handleReadMode(id)}
                      >
                        {dots}
                      </span>
                      <span
                        id={"more" + id}
                        style={{ display: "none" }}
                        onClick={() => this.handleReadMode(id)}
                      >
                        {restOfDescription}
                      </span>
                    </p>
                  </li>
                );
              })}
            </ul>
          </main>
        ),
      });
    });
  };

  handleCompleteChapter(id) {
    console.log(id);
  }

  handleReadMode(id) {
    let moreText = document.getElementById("more" + id);
    let dots = document.getElementById("dots" + id);

    if (moreText.style.display === "none") {
      dots.style.display = "none";
      moreText.style.display = "inline";
    } else {
      moreText.style.display = "none";
      dots.style.display = "inline";
    }
  }

  render() {
    return (
      <div className="common-body">
        <NavigationBar logout={this.props.logout} />
        {this.state.body}
      </div>
    );
  }
}

export default CoursePage;
