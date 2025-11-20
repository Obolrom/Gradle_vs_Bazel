package com.romix.feature.feat295

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat295Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat295UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat295FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat295UserSummary
)

data class Feat295UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat295NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat295Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat295Config = Feat295Config()
) {

    fun loadSnapshot(userId: Long): Feat295NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat295NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat295UserSummary {
        return Feat295UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat295FeedItem> {
        val result = java.util.ArrayList<Feat295FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat295FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat295UiMapper {

    fun mapToUi(model: List<Feat295FeedItem>): Feat295UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat295UiModel(
            header = UiText("Feat295 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat295UiModel =
        Feat295UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat295UiModel =
        Feat295UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat295UiModel =
        Feat295UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat295Service(
    private val repository: Feat295Repository,
    private val uiMapper: Feat295UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat295UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat295UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat295UserItem1(val user: CoreUser, val label: String)
data class Feat295UserItem2(val user: CoreUser, val label: String)
data class Feat295UserItem3(val user: CoreUser, val label: String)
data class Feat295UserItem4(val user: CoreUser, val label: String)
data class Feat295UserItem5(val user: CoreUser, val label: String)
data class Feat295UserItem6(val user: CoreUser, val label: String)
data class Feat295UserItem7(val user: CoreUser, val label: String)
data class Feat295UserItem8(val user: CoreUser, val label: String)
data class Feat295UserItem9(val user: CoreUser, val label: String)
data class Feat295UserItem10(val user: CoreUser, val label: String)

data class Feat295StateBlock1(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock2(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock3(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock4(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock5(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock6(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock7(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock8(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock9(val state: Feat295UiModel, val checksum: Int)
data class Feat295StateBlock10(val state: Feat295UiModel, val checksum: Int)

fun buildFeat295UserItem(user: CoreUser, index: Int): Feat295UserItem1 {
    return Feat295UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat295StateBlock(model: Feat295UiModel): Feat295StateBlock1 {
    return Feat295StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat295UserSummary> {
    val list = java.util.ArrayList<Feat295UserSummary>(users.size)
    for (user in users) {
        list += Feat295UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat295UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat295UiModel {
    val summaries = (0 until count).map {
        Feat295UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat295UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat295UiModel> {
    val models = java.util.ArrayList<Feat295UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat295AnalyticsEvent1(val name: String, val value: String)
data class Feat295AnalyticsEvent2(val name: String, val value: String)
data class Feat295AnalyticsEvent3(val name: String, val value: String)
data class Feat295AnalyticsEvent4(val name: String, val value: String)
data class Feat295AnalyticsEvent5(val name: String, val value: String)
data class Feat295AnalyticsEvent6(val name: String, val value: String)
data class Feat295AnalyticsEvent7(val name: String, val value: String)
data class Feat295AnalyticsEvent8(val name: String, val value: String)
data class Feat295AnalyticsEvent9(val name: String, val value: String)
data class Feat295AnalyticsEvent10(val name: String, val value: String)

fun logFeat295Event1(event: Feat295AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat295Event2(event: Feat295AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat295Event3(event: Feat295AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat295Event4(event: Feat295AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat295Event5(event: Feat295AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat295Event6(event: Feat295AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat295Event7(event: Feat295AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat295Event8(event: Feat295AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat295Event9(event: Feat295AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat295Event10(event: Feat295AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat295Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat295Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat295(u: CoreUser): Feat295Projection1 =
    Feat295Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat295Projection1> {
    val list = java.util.ArrayList<Feat295Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat295(u)
    }
    return list
}
