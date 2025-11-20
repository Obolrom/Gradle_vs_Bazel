package com.romix.feature.feat169

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat169Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat169UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat169FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat169UserSummary
)

data class Feat169UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat169NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat169Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat169Config = Feat169Config()
) {

    fun loadSnapshot(userId: Long): Feat169NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat169NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat169UserSummary {
        return Feat169UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat169FeedItem> {
        val result = java.util.ArrayList<Feat169FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat169FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat169UiMapper {

    fun mapToUi(model: List<Feat169FeedItem>): Feat169UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat169UiModel(
            header = UiText("Feat169 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat169UiModel =
        Feat169UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat169UiModel =
        Feat169UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat169UiModel =
        Feat169UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat169Service(
    private val repository: Feat169Repository,
    private val uiMapper: Feat169UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat169UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat169UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat169UserItem1(val user: CoreUser, val label: String)
data class Feat169UserItem2(val user: CoreUser, val label: String)
data class Feat169UserItem3(val user: CoreUser, val label: String)
data class Feat169UserItem4(val user: CoreUser, val label: String)
data class Feat169UserItem5(val user: CoreUser, val label: String)
data class Feat169UserItem6(val user: CoreUser, val label: String)
data class Feat169UserItem7(val user: CoreUser, val label: String)
data class Feat169UserItem8(val user: CoreUser, val label: String)
data class Feat169UserItem9(val user: CoreUser, val label: String)
data class Feat169UserItem10(val user: CoreUser, val label: String)

data class Feat169StateBlock1(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock2(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock3(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock4(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock5(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock6(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock7(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock8(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock9(val state: Feat169UiModel, val checksum: Int)
data class Feat169StateBlock10(val state: Feat169UiModel, val checksum: Int)

fun buildFeat169UserItem(user: CoreUser, index: Int): Feat169UserItem1 {
    return Feat169UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat169StateBlock(model: Feat169UiModel): Feat169StateBlock1 {
    return Feat169StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat169UserSummary> {
    val list = java.util.ArrayList<Feat169UserSummary>(users.size)
    for (user in users) {
        list += Feat169UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat169UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat169UiModel {
    val summaries = (0 until count).map {
        Feat169UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat169UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat169UiModel> {
    val models = java.util.ArrayList<Feat169UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat169AnalyticsEvent1(val name: String, val value: String)
data class Feat169AnalyticsEvent2(val name: String, val value: String)
data class Feat169AnalyticsEvent3(val name: String, val value: String)
data class Feat169AnalyticsEvent4(val name: String, val value: String)
data class Feat169AnalyticsEvent5(val name: String, val value: String)
data class Feat169AnalyticsEvent6(val name: String, val value: String)
data class Feat169AnalyticsEvent7(val name: String, val value: String)
data class Feat169AnalyticsEvent8(val name: String, val value: String)
data class Feat169AnalyticsEvent9(val name: String, val value: String)
data class Feat169AnalyticsEvent10(val name: String, val value: String)

fun logFeat169Event1(event: Feat169AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat169Event2(event: Feat169AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat169Event3(event: Feat169AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat169Event4(event: Feat169AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat169Event5(event: Feat169AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat169Event6(event: Feat169AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat169Event7(event: Feat169AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat169Event8(event: Feat169AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat169Event9(event: Feat169AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat169Event10(event: Feat169AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat169Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat169Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat169(u: CoreUser): Feat169Projection1 =
    Feat169Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat169Projection1> {
    val list = java.util.ArrayList<Feat169Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat169(u)
    }
    return list
}
