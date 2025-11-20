package com.romix.feature.feat410

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat410Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat410UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat410FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat410UserSummary
)

data class Feat410UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat410NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat410Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat410Config = Feat410Config()
) {

    fun loadSnapshot(userId: Long): Feat410NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat410NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat410UserSummary {
        return Feat410UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat410FeedItem> {
        val result = java.util.ArrayList<Feat410FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat410FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat410UiMapper {

    fun mapToUi(model: List<Feat410FeedItem>): Feat410UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat410UiModel(
            header = UiText("Feat410 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat410UiModel =
        Feat410UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat410UiModel =
        Feat410UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat410UiModel =
        Feat410UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat410Service(
    private val repository: Feat410Repository,
    private val uiMapper: Feat410UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat410UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat410UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat410UserItem1(val user: CoreUser, val label: String)
data class Feat410UserItem2(val user: CoreUser, val label: String)
data class Feat410UserItem3(val user: CoreUser, val label: String)
data class Feat410UserItem4(val user: CoreUser, val label: String)
data class Feat410UserItem5(val user: CoreUser, val label: String)
data class Feat410UserItem6(val user: CoreUser, val label: String)
data class Feat410UserItem7(val user: CoreUser, val label: String)
data class Feat410UserItem8(val user: CoreUser, val label: String)
data class Feat410UserItem9(val user: CoreUser, val label: String)
data class Feat410UserItem10(val user: CoreUser, val label: String)

data class Feat410StateBlock1(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock2(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock3(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock4(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock5(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock6(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock7(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock8(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock9(val state: Feat410UiModel, val checksum: Int)
data class Feat410StateBlock10(val state: Feat410UiModel, val checksum: Int)

fun buildFeat410UserItem(user: CoreUser, index: Int): Feat410UserItem1 {
    return Feat410UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat410StateBlock(model: Feat410UiModel): Feat410StateBlock1 {
    return Feat410StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat410UserSummary> {
    val list = java.util.ArrayList<Feat410UserSummary>(users.size)
    for (user in users) {
        list += Feat410UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat410UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat410UiModel {
    val summaries = (0 until count).map {
        Feat410UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat410UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat410UiModel> {
    val models = java.util.ArrayList<Feat410UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat410AnalyticsEvent1(val name: String, val value: String)
data class Feat410AnalyticsEvent2(val name: String, val value: String)
data class Feat410AnalyticsEvent3(val name: String, val value: String)
data class Feat410AnalyticsEvent4(val name: String, val value: String)
data class Feat410AnalyticsEvent5(val name: String, val value: String)
data class Feat410AnalyticsEvent6(val name: String, val value: String)
data class Feat410AnalyticsEvent7(val name: String, val value: String)
data class Feat410AnalyticsEvent8(val name: String, val value: String)
data class Feat410AnalyticsEvent9(val name: String, val value: String)
data class Feat410AnalyticsEvent10(val name: String, val value: String)

fun logFeat410Event1(event: Feat410AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat410Event2(event: Feat410AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat410Event3(event: Feat410AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat410Event4(event: Feat410AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat410Event5(event: Feat410AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat410Event6(event: Feat410AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat410Event7(event: Feat410AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat410Event8(event: Feat410AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat410Event9(event: Feat410AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat410Event10(event: Feat410AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat410Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat410Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat410(u: CoreUser): Feat410Projection1 =
    Feat410Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat410Projection1> {
    val list = java.util.ArrayList<Feat410Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat410(u)
    }
    return list
}
