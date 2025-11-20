package com.romix.feature.feat311

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat311Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat311UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat311FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat311UserSummary
)

data class Feat311UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat311NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat311Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat311Config = Feat311Config()
) {

    fun loadSnapshot(userId: Long): Feat311NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat311NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat311UserSummary {
        return Feat311UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat311FeedItem> {
        val result = java.util.ArrayList<Feat311FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat311FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat311UiMapper {

    fun mapToUi(model: List<Feat311FeedItem>): Feat311UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat311UiModel(
            header = UiText("Feat311 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat311UiModel =
        Feat311UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat311UiModel =
        Feat311UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat311UiModel =
        Feat311UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat311Service(
    private val repository: Feat311Repository,
    private val uiMapper: Feat311UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat311UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat311UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat311UserItem1(val user: CoreUser, val label: String)
data class Feat311UserItem2(val user: CoreUser, val label: String)
data class Feat311UserItem3(val user: CoreUser, val label: String)
data class Feat311UserItem4(val user: CoreUser, val label: String)
data class Feat311UserItem5(val user: CoreUser, val label: String)
data class Feat311UserItem6(val user: CoreUser, val label: String)
data class Feat311UserItem7(val user: CoreUser, val label: String)
data class Feat311UserItem8(val user: CoreUser, val label: String)
data class Feat311UserItem9(val user: CoreUser, val label: String)
data class Feat311UserItem10(val user: CoreUser, val label: String)

data class Feat311StateBlock1(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock2(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock3(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock4(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock5(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock6(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock7(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock8(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock9(val state: Feat311UiModel, val checksum: Int)
data class Feat311StateBlock10(val state: Feat311UiModel, val checksum: Int)

fun buildFeat311UserItem(user: CoreUser, index: Int): Feat311UserItem1 {
    return Feat311UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat311StateBlock(model: Feat311UiModel): Feat311StateBlock1 {
    return Feat311StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat311UserSummary> {
    val list = java.util.ArrayList<Feat311UserSummary>(users.size)
    for (user in users) {
        list += Feat311UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat311UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat311UiModel {
    val summaries = (0 until count).map {
        Feat311UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat311UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat311UiModel> {
    val models = java.util.ArrayList<Feat311UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat311AnalyticsEvent1(val name: String, val value: String)
data class Feat311AnalyticsEvent2(val name: String, val value: String)
data class Feat311AnalyticsEvent3(val name: String, val value: String)
data class Feat311AnalyticsEvent4(val name: String, val value: String)
data class Feat311AnalyticsEvent5(val name: String, val value: String)
data class Feat311AnalyticsEvent6(val name: String, val value: String)
data class Feat311AnalyticsEvent7(val name: String, val value: String)
data class Feat311AnalyticsEvent8(val name: String, val value: String)
data class Feat311AnalyticsEvent9(val name: String, val value: String)
data class Feat311AnalyticsEvent10(val name: String, val value: String)

fun logFeat311Event1(event: Feat311AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat311Event2(event: Feat311AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat311Event3(event: Feat311AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat311Event4(event: Feat311AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat311Event5(event: Feat311AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat311Event6(event: Feat311AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat311Event7(event: Feat311AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat311Event8(event: Feat311AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat311Event9(event: Feat311AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat311Event10(event: Feat311AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat311Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat311Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat311(u: CoreUser): Feat311Projection1 =
    Feat311Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat311Projection1> {
    val list = java.util.ArrayList<Feat311Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat311(u)
    }
    return list
}
