package com.romix.feature.feat692

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat692Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat692UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat692FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat692UserSummary
)

data class Feat692UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat692NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat692Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat692Config = Feat692Config()
) {

    fun loadSnapshot(userId: Long): Feat692NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat692NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat692UserSummary {
        return Feat692UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat692FeedItem> {
        val result = java.util.ArrayList<Feat692FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat692FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat692UiMapper {

    fun mapToUi(model: List<Feat692FeedItem>): Feat692UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat692UiModel(
            header = UiText("Feat692 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat692UiModel =
        Feat692UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat692UiModel =
        Feat692UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat692UiModel =
        Feat692UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat692Service(
    private val repository: Feat692Repository,
    private val uiMapper: Feat692UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat692UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat692UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat692UserItem1(val user: CoreUser, val label: String)
data class Feat692UserItem2(val user: CoreUser, val label: String)
data class Feat692UserItem3(val user: CoreUser, val label: String)
data class Feat692UserItem4(val user: CoreUser, val label: String)
data class Feat692UserItem5(val user: CoreUser, val label: String)
data class Feat692UserItem6(val user: CoreUser, val label: String)
data class Feat692UserItem7(val user: CoreUser, val label: String)
data class Feat692UserItem8(val user: CoreUser, val label: String)
data class Feat692UserItem9(val user: CoreUser, val label: String)
data class Feat692UserItem10(val user: CoreUser, val label: String)

data class Feat692StateBlock1(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock2(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock3(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock4(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock5(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock6(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock7(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock8(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock9(val state: Feat692UiModel, val checksum: Int)
data class Feat692StateBlock10(val state: Feat692UiModel, val checksum: Int)

fun buildFeat692UserItem(user: CoreUser, index: Int): Feat692UserItem1 {
    return Feat692UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat692StateBlock(model: Feat692UiModel): Feat692StateBlock1 {
    return Feat692StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat692UserSummary> {
    val list = java.util.ArrayList<Feat692UserSummary>(users.size)
    for (user in users) {
        list += Feat692UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat692UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat692UiModel {
    val summaries = (0 until count).map {
        Feat692UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat692UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat692UiModel> {
    val models = java.util.ArrayList<Feat692UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat692AnalyticsEvent1(val name: String, val value: String)
data class Feat692AnalyticsEvent2(val name: String, val value: String)
data class Feat692AnalyticsEvent3(val name: String, val value: String)
data class Feat692AnalyticsEvent4(val name: String, val value: String)
data class Feat692AnalyticsEvent5(val name: String, val value: String)
data class Feat692AnalyticsEvent6(val name: String, val value: String)
data class Feat692AnalyticsEvent7(val name: String, val value: String)
data class Feat692AnalyticsEvent8(val name: String, val value: String)
data class Feat692AnalyticsEvent9(val name: String, val value: String)
data class Feat692AnalyticsEvent10(val name: String, val value: String)

fun logFeat692Event1(event: Feat692AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat692Event2(event: Feat692AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat692Event3(event: Feat692AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat692Event4(event: Feat692AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat692Event5(event: Feat692AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat692Event6(event: Feat692AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat692Event7(event: Feat692AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat692Event8(event: Feat692AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat692Event9(event: Feat692AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat692Event10(event: Feat692AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat692Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat692Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat692(u: CoreUser): Feat692Projection1 =
    Feat692Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat692Projection1> {
    val list = java.util.ArrayList<Feat692Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat692(u)
    }
    return list
}
